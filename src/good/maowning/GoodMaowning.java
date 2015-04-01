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
			/* Call the Finder to gather images */
			int numOfNewUrls = Finder.findUrls().size();
			logger.info(String.format("Found <%d> new cat urls!", numOfNewUrls));
			
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
			
			/* Sleep */
			/* TODO: Reconfigure to be a 'sleep remainder of the the default' so that it runs every X ms instead */
			logger.info(String.format("Sleeping for [%d] milliseconds...", DEFAULT_SLEEP_MILLISECONDS));
			Thread.sleep(DEFAULT_SLEEP_MILLISECONDS);
		}
	}
}