package good.data;

import org.apache.log4j.Logger;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.sql.*;

import good.data.BasicDAO;
import good.data.GMImage;

public class ImageDAO extends BasicDAO {

	public ImageDAO() throws IOException {
		super();
	}

	protected GMDatabase getDatabaseEnum() {
		return GMDatabase.IMAGES;
	}
	
	//TODO: optimize to iteratively add to a sqlite batch and then evaluate them all (instead of evaluating one by one) 
	public void addImages(List<GMImage> gmImages) throws IOException, SQLException {
		for (GMImage gmImage : gmImages) {
			this.addImage(gmImage);
		}
	}
	
	public void addImage(GMImage gmImage) throws IOException, SQLException {
		logger.info("Adding url: " + gmImage.url + ", with categories: " + StringUtils.join(gmImage.categories));
		/* TODO: do some sanitization of the url */
		
		try (Statement stmt = dbConnection.createStatement()) {
			stmt.addBatch(String.format(
				"INSERT OR IGNORE INTO images(url) " + 
				"values('%s'); ",
				gmImage.url
			));
			for (String category : gmImage.categories) {
				stmt.addBatch(String.format(
					"INSERT OR IGNORE INTO categories(url_id, category) " +
					"SELECT id, '%s' FROM images WHERE url = '%s'; ",
					category,
					gmImage.url
				));
			}
			stmt.executeBatch();
		} catch (Exception e) {
			logger.error("Unable to insert url with categories");
			logger.error(e);
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
