package good.finder;

import org.apache.log4j.Logger;

import java.util.*;

public class RedditAPIWrapper {
	private final static Logger logger = Logger.getLogger(RedditAPIWrapper.class);
	
	public enum Subreddit {
		CATS("/r/cats"),
		AWW("/r/aww");
		
		private final String name;
		
		private Subreddit(String s) {
			name = s;
		}
		
		public String toString() {
			return name;
		}
	}
	
	public enum SubCategory {
		TOP("top"),
		HOT("hot");
		
		private final String name;
		
		private SubCategory(String s) {
			name = s;
		}
		
		public String toString() {
			return name;
		}
	}
	
	public enum Timespan {
		HOUR("hour"),
		DAY("day"),
		WEEK("week"),
		MONTH("month"),
		YEAR("year"),
		ALL("all");
		
		private final String name;
		
		private Timespan(String s) {
			name = s;
		}
		
		public String toString() {
			return name;
		}
	}

	public static List<String> getSubmissions(Subreddit sub, SubCategory category, Timespan time, int numOfEntries) {
		/* TODO */
		logger.debug(String.format("Subreddit <%s>, SubCategory <%s>, Timespan <%s>, Entries <%d>", sub, category, time, numOfEntries));
		return null;
	}
}