CREATE TABLE group_chats (
    id BIGSERIAL PRIMARY KEY,
    chat_id VARCHAR(100) UNIQUE NOT NULL,
    group_name VARCHAR(100) NOT NULL,
    owner_username VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_username) REFERENCES users(username)
);
CREATE TABLE group_chat_participants (
    id BIGSERIAL PRIMARY KEY,
    chat_id VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL,
    participant_type SMALLINT DEFAULT 0,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    UNIQUE (chat_id, username),
    FOREIGN KEY (chat_id) REFERENCES group_chats(chat_id) ON DELETE CASCADE,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);
