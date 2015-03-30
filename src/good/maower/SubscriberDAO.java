package good.maower;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.time.*;

import good.maower.BasicDAO;

public class SubscriberDAO extends BasicDAO {

	public SubscriberDAO() throws IOException {
		super();
	}

	protected GMDatabase getDatabaseEnum() {
		return GMDatabase.SUBSCRIBERS;
	}
	
	public List<String> getAll() throws IOException, SQLException {
		List<String> subscribers = new LinkedList<String>();
	
		logger.info("Getting all subscribers");
		
		/* Create the query */
		logger.debug("Executing query");
		try (Statement stmt = dbConnection.createStatement()) {
			ResultSet rs = stmt.executeQuery(
				"SELECT email " +
				"FROM subscribers;"
			);
			
			/* Parse the result for the url */
			while (rs.next()) {
				subscribers.add(rs.getString("email"));
			}
		}
		
		return subscribers;
	}
	
	public List<String> getDueSubscriptions() throws IOException, SQLException {
		List<String> subscribers = new LinkedList<String>();
	
		logger.info("Getting all subscribers");
		
		logger.debug("Calculating subscription time...");
		LocalDateTime currentTime = LocalDateTime.now();
		logger.debug(String.format("Grabbing unsent subscribers due before <%s>", currentTime.toString()));
		
		/* Create the query */
		/* TODO: Refactor query to better handle times that straddle the midnight turnovers */
		logger.debug("Executing query");
		try (Statement stmt = dbConnection.createStatement()) {
			ResultSet rs = stmt.executeQuery(String.format(
				"SELECT email " +
				"FROM subscribers " + 
				"WHERE last_date_sent != %d and send_time <= '%02d:%02d'; ",
				currentTime.getDayOfMonth(), currentTime.getHour(), currentTime.getMinute()
			));
			
			/* Parse the result for the url */
			while (rs.next()) {
				subscribers.add(rs.getString("email"));
			}
		}
		
		return subscribers;
	}
}