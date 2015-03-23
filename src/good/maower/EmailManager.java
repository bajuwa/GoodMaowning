package good.maower;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class EmailManager {
	private static final Logger logger = Logger.getLogger(EmailManager.class);
	private static final String EMAIL_PROP_FILE_NAME = "email.properties";

	public static void sendEmail(List<String> toAddresses, String subject, String body) throws IOException, MessagingException {
		/* Get our local email configurations */
		logger.debug("Loading email properties...");
		Properties emailProperties = loadProperties(EMAIL_PROP_FILE_NAME);
		
		/* Setup SMTP */
		logger.debug("Setting up SMTP...");
		Session session = Session.getInstance(
			emailProperties,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(
						emailProperties.getProperty("maower.username"), 
						emailProperties.getProperty("maower.password")
					);
				}
			}
		);
		
		/* Send the email */
		try {
			/* Create message */
			logger.debug("Creating email...");
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(emailProperties.getProperty("maower.email")));
			message.setSubject(subject);
			message.setText(body);
			
			/* Add subscribers */
			for (String toAddress : toAddresses) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
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
	
	/* TODO: Move to a reusable utils class */
	private static Properties loadProperties(String fileName) throws IOException {
		Properties propToLoad = new Properties();
		
		InputStream inputStream = Maower.class.getClassLoader().getResourceAsStream(fileName);
		if (inputStream != null) {
			propToLoad.load(inputStream);
		} else {
			throw new FileNotFoundException("Missing properties file: " + fileName);
		}
		
		return propToLoad;
	}
}