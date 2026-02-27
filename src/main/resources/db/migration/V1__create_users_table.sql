CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(50) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    email VARCHAR(100),
    status_message TEXT,
    display_pic_guid VARCHAR(100),
    avatar_pic_guid VARCHAR(100),
    presence SMALLINT DEFAULT 0,
    account_balance DECIMAL(10,2) DEFAULT 0.00,
    mig_level INTEGER DEFAULT 0,
    mig_level_image_url VARCHAR(255),
    contact_list_version INTEGER DEFAULT 0,
    contact_list_timestamp BIGINT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_user_id ON users(user_id);
CREATE INDEX idx_users_presence ON users(presence);
