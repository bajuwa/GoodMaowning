package good.maower;

import org.apache.log4j.Logger;

import java.io.IOException;
import javax.mail.MessagingException;
import java.util.*;

import good.maower.EmailManager;
import good.maower.ImageDAO;
import good.maower.SubscriberDAO;

/**
 * Sends emails to subscribers
 */
public class Maower {
	private static final Logger logger = Logger.getLogger(Maower.class);

	/* TODO: Tighten up the types of thrown Exceptions */
	public static void maow() throws Exception {
		logger.info("Good Maowning!");
		
		/* Subscribers */
		/* TODO: Refactor to call a 'SubscriberManager' class */
		logger.debug("Finding subscribers...");
		List<String> toAddresses = (new SubscriberDAO()).getDueSubscriptions();
		if (toAddresses.isEmpty()) {
			logger.warn("No subscribers found, aborting maowing");
			return;
		}
		
		/* TODO: Apply best effort when sending emails instead of fail-all */
		logger.debug("Generating content...");
		try (ImageDAO imageDao = new ImageDAO()) {
			for (String address : toAddresses) {
				/* Email Config: content */
				/* TODO: Refactor to call a 'MessageFormatter' class */
				String subject = "Good Maowning!";
				String messageBody = imageDao.getRandomUrl();
				
				/* Email Config: local properties */
				/* TODO: Move all 'sending email' code to a 'EmailManager' class */
				EmailManager.sendEmail(Arrays.asList(address), subject, messageBody);
				
				/* Mark the subscriber as having successfully been sent the email */
				/* TODO */
			}
		}
	}
}