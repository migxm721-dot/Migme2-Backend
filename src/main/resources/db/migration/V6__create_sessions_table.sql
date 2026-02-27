CREATE TABLE sessions (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(100) UNIQUE NOT NULL,
    username VARCHAR(50) NOT NULL,
    device_id VARCHAR(100),
    ip_address VARCHAR(50),
    user_agent TEXT,
    channel_id VARCHAR(100),
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);
CREATE INDEX idx_sessions_username ON sessions(username);
CREATE INDEX idx_sessions_active ON sessions(is_active);
