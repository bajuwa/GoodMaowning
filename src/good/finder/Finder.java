package good.finder;

import good.data.ImageDAO;
import good.finder.RedditAPIWrapper;
import good.finder.RedditAPIWrapper.*;

import java.util.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Scans internet for urls to add to system
 */
public class Finder {

	/**
	 * Scans through reddit image submissions to find potential cat urls
	 */
	public static void findUrls(Subreddit sub, SubCategory category, Timespan time, int numOfEntries) throws IOException, SQLException {
		List<String> urls = RedditAPIWrapper.getSubmissions(sub, category, time, numOfEntries);

		ImageDAO imageDao = new ImageDAO();
		for (String url : urls) {
			/* TODO: Filter out 'banned' words that often signal poor pictures (RIP, etc) */
			imageDao.addUrl(url);
		}
	}
}