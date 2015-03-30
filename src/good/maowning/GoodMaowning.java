package good.maowning;

import org.apache.log4j.Logger;

import good.maower.Maower;

import java.util.*;

public class GoodMaowning {
	private static final Logger logger = Logger.getLogger(GoodMaowning.class);
	
	private static final int DEFAULT_SLEEP_MILLISECONDS = 5 * 60 * 1000;
	
	public static void main(String[] args) throws InterruptedException {
		/* This program will run continuously until killed */
		while (true) {
			/* Call the Maower to send emails */
			logger.info("Maowing...");
			try {
				Maower.maow();
			} catch (Exception e) {
				logger.error("Encountered error during Maowing: ");
				logger.error(e);
			}
			
			/* Sleep */
			logger.info(String.format("Sleeping for [%d] milliseconds...", DEFAULT_SLEEP_MILLISECONDS));
			Thread.sleep(DEFAULT_SLEEP_MILLISECONDS);
		}
	}
}