package good.maower;

import org.apache.log4j.Logger;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * Sends emails to subscribers
 */
public class Maower {
	static Logger logger = Logger.getLogger(Maower.class);
	static final String EMAIL_PROP_FILE_NAME = "email.properties";

	public static void main(String[] args) throws IOException, MessagingException {
		logger.info("Good Maowning!");
		
		/* Subscribers */
		logger.debug("Finding subscribers...");
		String[] toAddresses = args;
		if (toAddresses.length == 0) {
			logger.warn("No subscribers found, aborting maowing");
			return;
		}
		
		/* Email Config: local properties */
		Properties emailProperties = new Properties();
		try {
			logger.debug("Loading email properties...");
			loadProperties(emailProperties, EMAIL_PROP_FILE_NAME);
		} catch (IOException e) {
			logger.error(e);
			throw e;
		}
		
		final String fromAddress = emailProperties.getProperty("maower.email");
		final String username = emailProperties.getProperty("maower.username");
		final String password = emailProperties.getProperty("maower.password");
		final String host = emailProperties.getProperty("maower.host");
		
		/* Email Config: content */
		logger.debug("Generating content...");
		String subject = "Good Maowning!";
		String messageBody = "http://i.imgur.com/bOd2iVK.jpg";
		
		/* Setup SMTP */
		logger.debug("Setting up SMTP...");
		Session session = Session.getInstance(emailProperties,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
		
		/* Send the email */
		try {
			/* Create message */
			logger.debug("Creating email...");
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.setSubject(subject);
			message.setText(messageBody);
			
			/* Add subscribers */
			for (int i = 0; i < toAddresses.length; i++) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddresses[i]));
			}
			
			/* Send the email */
			logger.debug("Sending email...");
			Transport.send(message);

			logger.info("Email sent!");
			
		} catch (MessagingException e) {
			logger.error(e);
			throw e;
		}
	}
	
	private static void loadProperties(Properties propToLoad, String fileName) throws IOException {
		InputStream inputStream = Maower.class.getClassLoader().getResourceAsStream(fileName);
		if (inputStream != null) {
			propToLoad.load(inputStream);
		} else {
			throw new FileNotFoundException("Missing properties file: " + fileName);
		}
	}
}