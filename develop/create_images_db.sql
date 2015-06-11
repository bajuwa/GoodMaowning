-- Call this script using the command: sqlite3 /data/images.db < create_images_db.sql

PRAGMA foreign_keys = ON;

BEGIN TRANSACTION;

CREATE TABLE images (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	url TEXT NOT NULL
);

CREATE TABLE categories (
	url_id INTEGER,
	category TEXT DEFAULT 'cute',
	PRIMARY KEY (url_id, category),
	FOREIGN KEY (url_id) REFERENCES images(id)
);

COMMIT;
