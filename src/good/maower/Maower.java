package good.maower;

import org.apache.log4j.Logger;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * Sends emails to subscribers
 */
public class Maower {
	static Logger logger = Logger.getLogger(Maower.class);

	public static void main(String[] args) {
		logger.info("Good Maowning!");
		
		/* Email Config */
		/* TODO: Move to a properties file */
		String toAddress = "goodmaowning@gmail.com";
		String fromAddress = "goodmaowning@gmail.com";
		String host = "localhost";
		String subject = "Good Maowning!";
		String messageBody = "http://i.imgur.com/bOd2iVK.jpg";
		
		/* Setup SMTP */
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		Session session = Session.getDefaultInstance(properties);
		
		/* Send the email */
		try {
			/* Create message */
			MimeMessage message = new MimeMessage(session);
			
			/* Format message */
			message.setFrom(new InternetAddress(fromAddress));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
			message.setSubject(subject);
			message.setText(messageBody);
			
			/* Send the email */
			Transport.send(message);
			logger.info("Email sent!");
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}