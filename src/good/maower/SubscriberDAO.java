package good.maower;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.sql.*;

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
}