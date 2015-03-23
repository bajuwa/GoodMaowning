package good.maower;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.sql.*;

public class ImageDAO {
	static final Logger logger = Logger.getLogger(ImageDAO.class);
	static final String DATABASE_PROP_FILE_NAME = "database.properties";
	
	public static String getRandomUrl() throws IOException, SQLException {
		logger.info("Getting random url");
		Connection dbConnection = connect();
		
		/* Create the query */
		logger.debug("Executing query");
		Statement stmt = dbConnection.createStatement();
		ResultSet rs = stmt.executeQuery(
			"SELECT url " +
			"FROM images " +
			"ORDER BY RANDOM() " +
			"LIMIT 1;"
		);
		
		/* Parse the result for the url */
		if (rs.next()) {
			return rs.getString("url");
		} else {
			logger.error("No URL record found");
			throw new RuntimeException("No URL record found");
		}
	}
	
	private static Connection connect() throws IOException {
		Properties databaseProperties = loadProperties(DATABASE_PROP_FILE_NAME);
		Connection dbConnection = null;
	
		/* Setup connection to our images database */
		try {
			logger.debug("Setting up database connection");
			Class.forName(databaseProperties.getProperty("connection.class"));
			dbConnection = DriverManager.getConnection(databaseProperties.getProperty("connection.string"));
		} catch (Exception e) {
			logger.error(e);
			throw new IOException("Unable to connect to images database");
		}
		
		return dbConnection;
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