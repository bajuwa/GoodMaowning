package good.finder;

import org.apache.log4j.Logger;

import good.data.ImageDAO;
import good.finder.RedditAPIWrapper;

import java.util.*;
import java.io.*;
import java.sql.SQLException;
import com.github.jreddit.entity.*;
import com.github.jreddit.utils.restclient.*;

/**
 * Scans internet for urls to add to system
 */
public class Finder {
	private static final Logger logger = Logger.getLogger(Finder.class);
	
	private static final String REDDIT_PROP_FILE_NAME = "reddit.properties";

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
	
	public static void wakeBot() throws IOException, SQLException {
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