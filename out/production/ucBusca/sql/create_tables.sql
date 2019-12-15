CREATE TABLE IF NOT EXISTS user (
    username VARCHAR(64) PRIMARY KEY,
    password VARCHAR(64) NOT NULL,
    is_admin BOOL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS notification (
    notification_id INTEGER PRIMARY KEY,
    message	 VARCHAR(256),
    is_new		 BOOL DEFAULT TRUE,
    user_username	 VARCHAR(64) NOT NULL,
    FOREIGN KEY (user_username) REFERENCES user (username) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS page (
    url	 VARCHAR(1024) PRIMARY KEY,
    title VARCHAR(128),
    quote VARCHAR(1024)
);

CREATE TABLE IF NOT EXISTS word (
    word VARCHAR(128) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS search_history (
    search	 VARCHAR(512) NOT NULL PRIMARY KEY,
    times_searched INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS search_history_user (
    search_history_search VARCHAR(512),
    user_username	 VARCHAR(64),
    FOREIGN KEY (search_history_search) REFERENCES search_history (search) ON DELETE CASCADE,
    FOREIGN KEY (user_username) REFERENCES user (username) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS word_page (
    word_word VARCHAR(128),
    page_url	 VARCHAR(1024),
    PRIMARY KEY(word_word, page_url),
    FOREIGN KEY (word_word) REFERENCES word (word) ON DELETE CASCADE,
    FOREIGN KEY (page_url) REFERENCES page (url) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS page_page (
    origin_url	 VARCHAR(1024),
    page_url VARCHAR(1024),
    PRIMARY KEY(origin_url, page_url),
    FOREIGN KEY (origin_url) REFERENCES page (url) ON DELETE CASCADE,
    FOREIGN KEY (page_url) REFERENCES page (url) ON DELETE CASCADE
);