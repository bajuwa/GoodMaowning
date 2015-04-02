package good.maowning;

import org.apache.log4j.Logger;

import good.maower.Maower;
import good.finder.Finder;
import good.finder.RedditAPIWrapper.*;

import java.util.*;

public class GoodMaowning {
	private static final Logger logger = Logger.getLogger(GoodMaowning.class);
	
	private static final int DEFAULT_MAOWER_SLEEP_MILLISECONDS = 5 * 60 * 1000;
	private static final int DEFAULT_FINDER_SLEEP_MILLISECONDS = 60 * 60 * 1000;
	
	public static void main(String[] args) throws InterruptedException {
		switch (args[0]) {
			case "maower":
				runMaower();
				break;
			case "finder":
				runFinder();
				break;
			default:
				logger.error("No component name specified, aborting Good Maowning");
		}
	}
	
	private static void runMaower() throws InterruptedException {
		logger.info("Starting Maower...");
		
		/* This program will run continuously until killed */
		while (true) {
			long startTime = System.currentTimeMillis();

			/* Call the Maower to send emails */
			logger.info("Maowing...");
			try {
				Maower.maow();
			} catch (Exception e) {
				logger.error("Encountered error during Maowing: ");
				logger.error(e);
			}
			
			/* Sleep for the remainder of our cycle time (if any time remains) */
			long totalTimeSpent = System.currentTimeMillis() - startTime;
			long timeToSleep = Math.max(0, DEFAULT_MAOWER_SLEEP_MILLISECONDS - totalTimeSpent);
			logger.info(String.format("Sleeping for [%d] milliseconds...", timeToSleep));
			Thread.sleep(timeToSleep);
		}
	}
	
	private static void runFinder() throws InterruptedException {
		logger.info("Starting Finder...");
		
		/* This program will run continuously until killed */
		while (true) {
			long startTime = System.currentTimeMillis();
		
			/* Call the Finder to gather images */
			/* TODO: Limit how often requests are being made */
			logger.info("Finding...");
			try {
				Finder.findUrls(
					Subreddit.CATS, 
					SubCategory.HOT, 
					Timespan.HOUR, 
					5
				);
				Finder.findUrls(
					Subreddit.CATS, 
					SubCategory.TOP, 
					Timespan.DAY, 
					10
				);
			} catch (Exception e) {
				logger.error("Encountered error during Finding: ");
				logger.error(e);
			}
			
			/* Sleep for the remainder of our cycle time (if any time remains) */
			long totalTimeSpent = System.currentTimeMillis() - startTime;
			long timeToSleep = Math.max(0, DEFAULT_FINDER_SLEEP_MILLISECONDS - totalTimeSpent);
			logger.info(String.format("Sleeping for [%d] milliseconds...", timeToSleep));
			Thread.sleep(timeToSleep);
		}
	}
}