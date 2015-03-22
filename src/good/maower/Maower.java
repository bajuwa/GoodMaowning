package good.maower;

import org.apache.log4j.Logger;

import java.io.IOException;
import javax.mail.MessagingException;

import good.maower.EmailManager;

/**
 * Sends emails to subscribers
 */
public class Maower {
	static Logger logger = Logger.getLogger(Maower.class);

	public static void main(String[] args) throws IOException, MessagingException {
		logger.info("Good Maowning!");
		
		/* Subscribers */
		/* TODO: Refactor to call a 'SubscriberManager' class */
		logger.debug("Finding subscribers...");
		String[] toAddresses = args;
		if (toAddresses.length == 0) {
			logger.warn("No subscribers found, aborting maowing");
			return;
		}
		
		/* Email Config: content */
		/* TODO: Refactor to call a 'MessageFormatter' class */
		logger.debug("Generating content...");
		String subject = "Good Maowning!";
		String messageBody = "http://i.imgur.com/bOd2iVK.jpg";
		
		/* Email Config: local properties */
		/* TODO: Move all 'sending email' code to a 'EmailManager' class */
		EmailManager.sendEmail(toAddresses, subject, messageBody);
	}
}