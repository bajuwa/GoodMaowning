-- Call this script using the command: sqlite3 /data/subscribers.db < create_subscribers_db.sql

PRAGMA foreign_keys = ON;

BEGIN TRANSACTION;

CREATE TABLE subscribers (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	email TEXT UNIQUE NOT NULL
);

COMMIT;