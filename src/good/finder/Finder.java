package good.finder;

import java.util.*;

/**
 * Scans internet for urls to add to system
 */
public class Finder {

	/**
	 * Scans through reddit image submissions to find potential cat urls
	 */
	public static List<String> findUrls() {
		/* TODO: Get some cats from /r/cats */
		return RedditAPIWrapper.getSubmissions(
			RedditAPIWrapper.Subreddit.CATS, 
			RedditAPIWrapper.SubCategory.HOT, 
			RedditAPIWrapper.Timespan.DAY, 
			5
		);
		
		/* TODO: Get some cats for /r/aww */
	}
}