package good.finder;

import org.apache.log4j.Logger;

import java.util.*;
 
/* HTTP Imports */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RedditAPIWrapper {
	private final static Logger logger = Logger.getLogger(RedditAPIWrapper.class);
	
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
		try {
			String response = sendHttpGet(formUrl(sub, category, time, numOfEntries));
		} catch (IOException e) {
			logger.error(e);
			return new ArrayList<String>();
		}
		
		/* TODO: Parse into more usable form? */
		
		return new ArrayList<String>();
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