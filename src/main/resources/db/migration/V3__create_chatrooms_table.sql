CREATE TABLE chat_rooms (
    id BIGSERIAL PRIMARY KEY,
    chat_id VARCHAR(100) UNIQUE NOT NULL,
    room_name VARCHAR(100) NOT NULL,
    description TEXT,
    creator_username VARCHAR(50),
    group_id INTEGER,
    category_id INTEGER,
    is_public BOOLEAN DEFAULT TRUE,
    max_participants INTEGER DEFAULT 100,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_username) REFERENCES users(username)
);
CREATE TABLE chat_room_participants (
    id BIGSERIAL PRIMARY KEY,
    chat_id VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL,
    participant_type SMALLINT DEFAULT 0,
    is_admin BOOLEAN DEFAULT FALSE,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    left_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    UNIQUE (chat_id, username),
    FOREIGN KEY (chat_id) REFERENCES chat_rooms(chat_id) ON DELETE CASCADE,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);
CREATE INDEX idx_chatroom_participants_chat ON chat_room_participants(chat_id);
CREATE INDEX idx_chatroom_participants_user ON chat_room_participants(username);
