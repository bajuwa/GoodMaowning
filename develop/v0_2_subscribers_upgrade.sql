ALTER TABLE subscribers
ADD COLUMN send_time TEXT NOT NULL DEFAULT '09:00';

ALTER TABLE subscribers
ADD COLUMN last_date_sent TEXT;