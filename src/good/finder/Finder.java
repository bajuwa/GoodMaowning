package good.finder;

import org.apache.log4j.Logger;

import good.data.ImageDAO;
import good.finder.RedditAPIWrapper;

import java.util.*;
import java.io.*;
import java.sql.SQLException;
import com.github.jreddit.entity.*;
import com.github.jreddit.action.*;
import com.github.jreddit.utils.restclient.*;

/**
 * Scans internet for urls to add to system
 */
public class Finder {
	private static final Logger logger = Logger.getLogger(Finder.class);
	
	private static final String REDDIT_PROP_FILE_NAME = "reddit.properties";
	private static final String BOT_KEYPHRASE = "It's a kitty!";
	
	private String lastSeenCommentId;

	/**
	 * Scans through reddit image submissions to find potential cat urls
	 */
	public static void findUrls(RedditAPIWrapper.Subreddit sub, RedditAPIWrapper.SubCategory category, RedditAPIWrapper.Timespan time, int numOfEntries) throws IOException, SQLException {
		List<String> urls = RedditAPIWrapper.getSubmissions(sub, category, time, numOfEntries);

		ImageDAO imageDao = new ImageDAO();
		for (String url : urls) {
			/* TODO: Filter out 'banned' words that often signal poor pictures (RIP, etc) */
			imageDao.addUrl(url);
		}
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
				/* Get the latest batch of comments made since our bot last woke up */
				List<Comment> comments = RedditAPIWrapper.getNewestCommentsBefore(restClient, user, RedditAPIWrapper.Subreddit.CATS, lastSeenCommentId, 200);
				logger.debug("Got comments: " + comments);
			
				logger.info("Looking for key phrases...");
				for (Comment comment : comments) {
					/* TODO: If anyone says "It's a kitty!" respond to them */
					if (comment.getBody().equals(BOT_KEYPHRASE)) {
						logger.info(String.format("Comment <%s> with text body <%s> matched keyphrase [%s]", comment.getFullName(), comment.getBody(), BOT_KEYPHRASE));
						
						SubmitActions reply = new SubmitActions(restClient, user);
						reply.comment(comment.getFullName(), "Yes, it is!");
					}
				}
						
				/* TODO: For any of the bots posts with positive karma, add the image to db */
				logger.info("Storing images...");
				
				/* The last comment id should be stored for next call */
				if (comments.size() > 0) {
					lastSeenCommentId = comments.get(0).getFullName();
					logger.debug("Updated comment landmark: " + lastSeenCommentId);
				}
			}
			
		} catch (Exception e) {
			logger.error(e);
		}
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
}