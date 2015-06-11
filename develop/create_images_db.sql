-- Call this script using the command: sqlite3 /data/images.db < create_images_db.sql

PRAGMA foreign_keys = ON;

BEGIN TRANSACTION;

CREATE TABLE images (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	url TEXT NOT NULL,
	category TEXT DEFAULT 'cute'
);

COMMIT;
