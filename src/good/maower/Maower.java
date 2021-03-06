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
	static final Logger logger = Logger.getLogger(Maower.class);

	public static void main(String[] args) throws Exception {
		logger.info("Good Maowning!");
		
		/* Subscribers */
		/* TODO: Refactor to call a 'SubscriberManager' class */
		logger.debug("Finding subscribers...");
		List<String> toAddresses = (new SubscriberDAO()).getAll();
		if (toAddresses.isEmpty()) {
			logger.warn("No subscribers found, aborting maowing");
			return;
		}
		
		logger.debug("Generating content...");
		for (String address : toAddresses) {
			/* Email Config: content */
			/* TODO: Refactor to call a 'MessageFormatter' class */
			String subject = "Good Maowning!";
			String messageBody = (new ImageDAO()).getRandomUrl();
			
			/* Email Config: local properties */
			/* TODO: Move all 'sending email' code to a 'EmailManager' class */
			EmailManager.sendEmail(Arrays.asList(address), subject, messageBody);
		}
	}
}