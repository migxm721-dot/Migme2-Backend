CREATE TABLE contacts (
    id BIGSERIAL PRIMARY KEY,
    user_username VARCHAR(50) NOT NULL,
    contact_username VARCHAR(50) NOT NULL,
    contact_id INTEGER,
    display_name VARCHAR(100),
    group_id INTEGER,
    is_favorite BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_username, contact_username),
    FOREIGN KEY (user_username) REFERENCES users(username) ON DELETE CASCADE,
    FOREIGN KEY (contact_username) REFERENCES users(username) ON DELETE CASCADE
);
CREATE TABLE contact_groups (
    id BIGSERIAL PRIMARY KEY,
    group_id INTEGER UNIQUE NOT NULL,
    user_username VARCHAR(50) NOT NULL,
    group_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_username) REFERENCES users(username) ON DELETE CASCADE
);
