package good.finder;

import good.data.ImageDAO;

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
	public static void findUrls() throws IOException, SQLException {
		/* TODO: Get some cats from /r/cats */
		List<String> urls = RedditAPIWrapper.getSubmissions(
			RedditAPIWrapper.Subreddit.CATS, 
			RedditAPIWrapper.SubCategory.HOT, 
			RedditAPIWrapper.Timespan.HOUR, 
			5
		);
		
		/* TODO: Get some cats for /r/aww */
		
		ImageDAO imageDao = new ImageDAO();
		for (String url : urls) {
			/* TODO: Filter out 'banned' words that often signal poor pictures (RIP, etc) */
			imageDao.addUrl(url);
		}
	}
}