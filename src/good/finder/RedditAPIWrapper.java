package good.finder;

import org.apache.log4j.Logger;

import java.util.*;
import org.json.*;
 
/* HTTP Imports */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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
				/* Only return the url if it is from an image host (no self.sub, youtube, etc submissions) */
				if (submission.getString("domain").equals(IMAGE_HOST_DOMAIN)) {
					/* Make sure we use i.imgur to get the image, not the imgur page */
					String url = submission.getString("url").replace("//imgur", "//i.imgur");
					/* Make sure there is an image type extension as well (default to jpg) */
					if (!url.matches(".*imgur\.com/.*\..*")) {
						url.concat(".jpg");
					}
					/* Add our 'massaged' url to the return list */
					urls.add(url);
				}
			}
			return urls;
		} catch (Exception e) {
			logger.error("Unable to parse response json, skipping submission grab");
			return new ArrayList<String>();
		}
		
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
		logger.debug("Returning response: " + finalResponse);
		return finalResponse;
	}
}