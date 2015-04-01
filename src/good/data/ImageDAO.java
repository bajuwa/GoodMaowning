package good.data;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.sql.*;

import good.data.BasicDAO;

public class ImageDAO extends BasicDAO {

	public ImageDAO() throws IOException {
		super();
	}

	protected GMDatabase getDatabaseEnum() {
		return GMDatabase.IMAGES;
	}
	
	public void addUrl(String url) throws IOException, SQLException {
		logger.info("Adding url: " + url);
		/* TODO: do some sanitization of the url */
		
		try (Statement stmt = dbConnection.createStatement()) {
			stmt.executeUpdate(String.format(
				"INSERT OR IGNORE INTO images(url) " + 
				"values('%s'); ",
				url
			));
		}
	}
	
	public String getRandomUrl() throws IOException, SQLException {
		logger.info("Getting random url");
		
		/* Create the query */
		try (Statement stmt = dbConnection.createStatement()) {
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
	}
}