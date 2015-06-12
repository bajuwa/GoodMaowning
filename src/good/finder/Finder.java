package good.finder;

import org.apache.log4j.Logger;
import org.apache.commons.lang3.StringUtils;

import good.data.ImageDAO;
import good.data.GMImage;
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
	private static final String BOT_KEYPHRASE = "It's an{0,1} ([a-zA-Z\\- ]+) kitty!";
	
	private String lastSeenCommentId;

	/**
	 * Scans through reddit image submissions to find potential cat urls
	 */
	public static void findUrls(RedditAPIWrapper.Subreddit sub, RedditAPIWrapper.SubCategory category, RedditAPIWrapper.Timespan time, int numOfEntries) throws IOException, SQLException {
		/* TODO: Filter out 'banned' words that often signal poor pictures (RIP, etc) */
		// TODO: Don't even think there should be an arbitrarily categorized set of urls
		//(new ImageDAO()).addImages(RedditAPIWrapper.getSubmissions(sub, category, time, numOfEntries));
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
				List<Pair<Comment, GMImage>> commentImagePairs = getKeyphrasedComments(restClient, user);
				for (Pair<Comment, GMImage> commentImagePair : commentImagePairs) {
					/* TODO: Move the comment message body formatting to a separate class */
					SubmitActions reply = new SubmitActions(restClient, user);
					String categoriesStr = StringUtils.join(commentImagePair.right.categories);
					String message = String.format(
						"A%s %s kitty, eh?  I'll keep that in mind!", 
						isVowel(categoriesStr.charAt(0)) ? "n" : "",
						String.join(", ", commentImagePair.right.categories)
					);
					reply.comment(commentImagePair.left.getFullName(), message);
					
					/* TODO: JReddit doesn't seem to support getting the link url yet */
					linkIds.add(commentImagePair.left.getLinkId());
				}
						
				/* Add any urls that were commented on to the image db */
				if (linkIds.size() > 0) {
					logger.info("Storing images...");
					ImageDAO imageDao = new ImageDAO();
					List<Submission> submissions = RedditAPIWrapper.getSubmissionsByIds(restClient, user, linkIds);
					// TODO: I really hope this api returns them in the same order that is asked....
					for (int i = 0; i < submissions.size(); i++) {
						try {
							GMImage image = commentImagePairs.get(i).right;
							image.url = RedditAPIWrapper.formatImgurUrl(submissions.get(i).getURL());
							imageDao.addImage(image);
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
	 * Finds all comments since our last search that match our keyphrase, while also returning the nested categoires within the keyphrase.
	 */
	private List<Pair<Comment, GMImage>> getKeyphrasedComments(RestClient client, User user) {
		List<Comment> comments = RedditAPIWrapper.getNewestCommentsBefore(client, user, RedditAPIWrapper.Subreddit.CATS, lastSeenCommentId, 1000);
		logger.debug("Got comments: " + comments);
	
		logger.info("Looking for key phrases...");
		List<Pair<Comment, GMImage>> keyphraseComments = new ArrayList<Pair<Comment, GMImage>>();
		for (Comment comment : comments) {
			Pattern kittyPattern = Pattern.compile(BOT_KEYPHRASE);
			Matcher kittyMatcher = kittyPattern.matcher(comment.getBody());
			if (kittyMatcher.find()) {
				List<String> categories = Arrays.asList(kittyMatcher.group(1).toLowerCase().split(" "));
				logger.info(String.format("Comment <%s> with text body <%s> matched keyphrase", comment.getFullName(), comment.getBody()));
				keyphraseComments.add(new Pair<Comment, GMImage>(comment, new GMImage("", categories)));
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

	private class Pair<L, R> {
		public final L left;
		public final R right;
		public Pair(L l, R r) {
			this.left = l;
			this.right = r;
		}
	}
}
