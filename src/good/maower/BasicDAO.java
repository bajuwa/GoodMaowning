package good.maower;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.sql.*;

public abstract class BasicDAO {
	protected final Logger logger = Logger.getLogger(getClass());
	private final String DATABASE_PROP_FILE_NAME = "database.properties";
	
	protected enum GMDatabase {
		IMAGES
	}
	
	protected abstract GMDatabase getDatabaseEnum();
	
	protected Connection connect() throws IOException {
		Properties databaseProperties = loadProperties(DATABASE_PROP_FILE_NAME);
		Connection dbConnection = null;
		
		/* Setup connection to our images database */
		try {
			logger.debug("Setting up database connection");
			Class.forName(databaseProperties.getProperty("connection.class"));
			dbConnection = DriverManager.getConnection(
				databaseProperties.getProperty("connection.string") + getConnectionString(databaseProperties)
			);
		} catch (Exception e) {
			logger.error(e);
			throw new IOException("Unable to connect to images database");
		}
		
		return dbConnection;
	}
	
	private String getConnectionString(Properties databaseProperties) {
		/* Translate the given enum to a connection string */
		GMDatabase db = getDatabaseEnum();
		switch (db) {
			case IMAGES:
				return databaseProperties.getProperty("connection.db.images");
			default:
				throw new RuntimeException("No database found for enum: " + db);
		}
	}
	
	/* TODO: Move to a reusable utils class */
	private Properties loadProperties(String fileName) throws IOException {
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