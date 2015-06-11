package good.finder;

import org.apache.log4j.Logger;

import good.data.ImageDAO;
import good.finder.RedditAPIWrapper;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;
import java.sql.SQLException;
import java.lang.IllegalArgumentException;

import com.github.jreddit.entity.*;
import com.github.jreddit.action.*;
import com.github.jreddit.utils.restclient.*;

/**
 * Scans internet for urls to add to system
 */
public class Finder {
	private static final Logger logger = Logger.getLogger(Finder.class);
	
	private static final String REDDIT_PROP_FILE_NAME = "reddit.properties";
	private static final String BOT_KEYPHRASE = "It's an{0,1} ([a-zA-Z\\-]+) kitty!";
	
	private String lastSeenCommentId;

	/**
	 * Scans through reddit image submissions to find potential cat urls
	 */
	public static void findUrls(RedditAPIWrapper.Subreddit sub, RedditAPIWrapper.SubCategory category, RedditAPIWrapper.Timespan time, int numOfEntries) throws IOException, SQLException {
		/* TODO: Filter out 'banned' words that often signal poor pictures (RIP, etc) */
		(new ImageDAO()).addUrls(RedditAPIWrapper.getSubmissions(sub, category, time, numOfEntries));
	}
	
	public void wakeBot() throws IOException, SQLException {
		Properties redditProperties = loadProperties(REDDIT_PROP_FILE_NAME);
		
		// Initialize REST Client
		RestClient restClient = new HttpRestClient();
		restClient.setUserAgent("GoodMaowning/0.2 by bajuwa");

		// Connect the user 
		User user = new User(
			restClient, 
			redditProperties.getProperty("bot.username"), 
			redditProperties.getProperty("bot.password")
		);
		
		try {
			user.connect();
			logger.info("Successfully connected to reddit bot");
			
			logger.info("Getting user comments...");
			if (lastSeenCommentId == null) {
				/* If the bot has not run yet, get the latest comment to use as a marker */
				List<Comment> comments = RedditAPIWrapper.getNewestComments(restClient, user, RedditAPIWrapper.Subreddit.CATS, 1);
				lastSeenCommentId = comments.get(0).getFullName();
				logger.debug("Initial comment landmark: " + lastSeenCommentId);
			} else {
				/* Handle any new comments that are tagging reddit images in /r/cats */
				List<String> linkIds = new ArrayList<String>();
				List<GMImage> gmImages = getKeyphrasedComments(restClient, user);
				for (GMImage gmImage : gmImages) {
					/* TODO: Move the comment message body formatting to a separate class */
					SubmitActions reply = new SubmitActions(restClient, user);
					String message = String.format(
						"A%s %s kitty, eh?  I'll keep that in mind!", 
						isVowel(gmImage.tag.charAt(0)) ? "n" : "",
						gmImage.tag
					);
					reply.comment(gmImage.comment.getFullName(), message);
					
					/* TODO: JReddit doesn't seem to support getting the link url yet */
					linkIds.add(gmImage.comment.getLinkId());
				}
						
				/* Add any urls that were commented on to the image db */
				if (linkIds.size() > 0) {
					logger.info("Storing images...");
					ImageDAO imageDao = new ImageDAO();
					List<Submission> submissions = RedditAPIWrapper.getSubmissionsByIds(restClient, user, linkIds);
					// TODO: I really hope this api returns them in the same order that is asked....
					for (int i = 0; i < submissions.size(); i++) {
						try {
							imageDao.addUrl(
								RedditAPIWrapper.formatImgurUrl(submissions.get(i).getURL()),
								gmImages.get(i).tag
							);
						} catch (IllegalArgumentException e) {
							logger.warn(e);
						}
					}
				}
			}
			
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	/**
	 * Finds all comments since our last search that match our keyphrase, while also returning the nested 'tag' within the keyphrase.
	 */
	private List<GMImage> getKeyphrasedComments(RestClient client, User user) {
		List<Comment> comments = RedditAPIWrapper.getNewestCommentsBefore(client, user, RedditAPIWrapper.Subreddit.CATS, lastSeenCommentId, 1000);
		logger.debug("Got comments: " + comments);
	
		logger.info("Looking for key phrases...");
		List<GMImage> keyphraseComments = new ArrayList<GMImage>();
		for (Comment comment : comments) {
			Pattern kittyPattern = Pattern.compile(BOT_KEYPHRASE);
			Matcher kittyMatcher = kittyPattern.matcher(comment.getBody());
			if (kittyMatcher.find()) {
				String tag = kittyMatcher.group(1).toLowerCase();
				logger.info(String.format("Comment <%s> with text body <%s> matched keyphrase with tag <%s>", comment.getFullName(), comment.getBody(), tag));
				keyphraseComments.add(new GMImage(comment, tag));
			}
		}
				
		/* The last comment id should be stored for next call */
		if (comments.size() > 0) {
			lastSeenCommentId = comments.get(0).getFullName();
			logger.debug("Updated comment landmark: " + lastSeenCommentId);
		}
		
		return keyphraseComments;
	}
	
	/* TODO: Move to a reusable utils class */
	private static Properties loadProperties(String fileName) throws IOException {
		Properties propToLoad = new Properties();
		
		InputStream inputStream = Finder.class.getClassLoader().getResourceAsStream(fileName);
		if (inputStream != null) {
			propToLoad.load(inputStream);
		} else {
			throw new FileNotFoundException("Missing properties file: " + fileName);
		}
		
		return propToLoad;
	}

	private boolean isVowel(char c) {
		return "aeiou".indexOf(Character.toLowerCase(c)) > 0;
	}

	private class GMImage {
		public Comment comment;
		public String tag;

		public GMImage(Comment c, String t) {
			this.comment = c;
			this.tag = t;
		}
	}
}
