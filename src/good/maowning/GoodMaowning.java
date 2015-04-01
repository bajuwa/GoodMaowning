package good.maowning;

import org.apache.log4j.Logger;

import good.maower.Maower;
import good.finder.Finder;

import java.util.*;

public class GoodMaowning {
	private static final Logger logger = Logger.getLogger(GoodMaowning.class);
	
	private static final int DEFAULT_SLEEP_MILLISECONDS = 5 * 60 * 1000;
	
	public static void main(String[] args) throws InterruptedException {
		/* This program will run continuously until killed */
		while (true) {
			long startTime = System.currentTimeMillis();
		
			/* Call the Finder to gather images */
			logger.info("Finding...");
			try {
				Finder.findUrls();
			} catch (Exception e) {
				logger.error("Encountered error during Finding: ");
				logger.error(e);
			}
			
			/* Call Subber to manage subscription requests */
			/* TODO */
		
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
			long timeToSleep = Math.max(0, DEFAULT_SLEEP_MILLISECONDS - totalTimeSpent);
			logger.info(String.format("Sleeping for [%d] milliseconds...", timeToSleep));
			Thread.sleep(timeToSleep);
		}
	}
}