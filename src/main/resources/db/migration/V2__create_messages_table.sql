CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    message_id VARCHAR(100) UNIQUE NOT NULL,
    message_type SMALLINT NOT NULL,
    source_username VARCHAR(50) NOT NULL,
    destination_type SMALLINT NOT NULL,
    destination VARCHAR(100) NOT NULL,
    content_type SMALLINT NOT NULL,
    message_body TEXT,
    filename VARCHAR(255),
    display_pic_guid VARCHAR(100),
    timestamp BIGINT NOT NULL,
    delivery_status SMALLINT DEFAULT 0,
    message_direction SMALLINT DEFAULT 0,
    is_server_info BOOLEAN DEFAULT FALSE,
    pinned_type SMALLINT DEFAULT 0,
    emote_content_type SMALLINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (source_username) REFERENCES users(username) ON DELETE CASCADE
);
CREATE INDEX idx_messages_destination ON messages(destination);
CREATE INDEX idx_messages_timestamp ON messages(timestamp DESC);
CREATE INDEX idx_messages_source ON messages(source_username);
CREATE INDEX idx_messages_message_id ON messages(message_id);
