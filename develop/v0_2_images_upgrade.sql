PRAGMA foreign_keys = on;

CREATE TABLE categories (
        url_id INTEGER,
        category TEXT DEFAULT 'cute',
        PRIMARY KEY (url_id, category),
        FOREIGN KEY (url_id) REFERENCES images(id)
);
