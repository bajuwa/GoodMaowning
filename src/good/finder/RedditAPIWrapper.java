package good.finder;

import org.apache.log4j.Logger;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import org.json.*;
import java.lang.IllegalArgumentException;
 
/* HTTP Imports */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.github.jreddit.entity.*;
import com.github.jreddit.utils.restclient.*;
import com.github.jreddit.retrieval.*;

public class RedditAPIWrapper {
	private final static Logger logger = Logger.getLogger(RedditAPIWrapper.class);
	
	private final static String IMAGE_HOST_DOMAIN = "imgur.com";
	
	public enum Subreddit {
		CATS("cats"),
		AWW("aww");
		
		private final String name;
		private Subreddit(String s) {name = s;}
		public String toString() {return name;}
	}
	
	public enum SubCategory {
		TOP("top"),
		HOT("hot");
		
		private final String name;
		private SubCategory(String s) {name = s;}
		public String toString() {return name;}
	}
	
	public enum Timespan {
		HOUR("hour"),
		DAY("day"),
		WEEK("week"),
		MONTH("month"),
		YEAR("year"),
		ALL("all");
		
		private final String name;
		private Timespan(String s) {name = s;}
		public String toString() {return name;}
	}

	/* Refactor to use the new jreddit package */
	public static List<String> getSubmissions(Subreddit sub, SubCategory category, Timespan time, int numOfEntries) {
		/* Send our reddit api request */
		String response;
		try {
			response = sendHttpGet(formUrl(sub, category, time, numOfEntries));
		} catch (IOException e) {
			logger.error(e);
			return new ArrayList<String>();
		}
		
		/* TODO: Parse the json into a local object with only the needed information */
		try {
			List<String> urls = new ArrayList<String>();
			JSONObject json = new JSONObject(response);
			JSONArray listing = (JSONArray) ((JSONObject) json.get("data")).get("children");
			for (int i = 0; i < listing.length(); i++) {
				JSONObject submission = (JSONObject) ((JSONObject) listing.getJSONObject(i)).get("data");
				try {
					urls.add(formatImgurUrl(submission.getString("url")));
				} catch (IllegalArgumentException e) {
					logger.warn(e);
				}
			}
			return urls;
		} catch (Exception e) {
			logger.error("Unable to parse response json, skipping submission grab");
			return new ArrayList<String>();
		}
	}
	
	public static List<Submission> getSubmissionsByIds(RestClient client, User user, List<String> linkIds) {
		return (new Submissions(client, user)).parse(
			String.format(
				"/by_id/%s.json", StringUtils.join(linkIds, ",")
			)
		);
	}
	
	public static List<Comment> getNewestCommentsBefore(RestClient client, User user, Subreddit sub, String beforeId, int limit) {
		return (new Comments(client, user)).parseBreadth(
			String.format(
				"/r/%s/comments.json?before=%s&limit=%d&sort=new&depth=1",
				sub, beforeId, limit
			)
		);
	}
	
	public static List<Comment> getNewestComments(RestClient client, User user, Subreddit sub, int limit) {
		return (new Comments(client, user)).parseBreadth(
			String.format(
				"/r/%s/comments.json?limit=%d&sort=new",
				sub, limit
			)
		);
	}
	
	public static String formatImgurUrl(String originalUrl) throws IllegalArgumentException {
		String newUrl = originalUrl;
		
		/* Make sure we are dealing with an imgur domain url, and its not an album */
		if (!newUrl.matches(".*imgur\\.com.*") || newUrl.matches(".*\\/a\\/.*")) {
			throw new IllegalArgumentException("Unable to format url <" + originalUrl + "> in to a valid imgur format");
		}
		
		/* Make sure we use i.imgur to get the image, not the imgur page */
		if (!newUrl.matches(".*i\\.imgur\\.com.*")) {
			newUrl = newUrl.replace("//imgur", "//i.imgur");
		}
		
		/* Make sure there is an image type extension as well (default to jpg) */
		if (!newUrl.matches(".*imgur\\.com\\/.*\\..+")) {
			newUrl = newUrl.concat(".jpg");
		}
	
		return newUrl;
	}
	
	
	private static String formUrl(Subreddit sub, SubCategory category, Timespan time, int numOfEntries) {
		return String.format("http://www.reddit.com/r/%s/%s.json?t=%s&limit=%d", sub, category, time, numOfEntries);
	}
	
	private static String sendHttpGet(String url) throws IOException {
		logger.debug("Send HTTP Get request to: " + url);
	
		/* Setup connection to our url */
		URL urlObject = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
		
		/* Get Response via Stream */
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		StringBuffer response = new StringBuffer();
		
		/* Read response into string buffer */
		while ((line = reader.readLine()) != null) {
			response.append(line);
		}
		
		String finalResponse = response.toString();
		//logger.debug("Returning response: " + finalResponse);
		return finalResponse;
	}
}