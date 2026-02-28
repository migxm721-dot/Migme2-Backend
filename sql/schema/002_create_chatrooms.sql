-- Chatrooms table
CREATE TABLE IF NOT EXISTS chatrooms (
    id BIGSERIAL PRIMARY KEY,
    room_name VARCHAR(100) UNIQUE NOT NULL,
    topic TEXT,
    description TEXT,
    owner_username VARCHAR(50) NOT NULL,
    max_members INT DEFAULT 100,
    current_members INT DEFAULT 0,
    is_public BOOLEAN DEFAULT TRUE,
    is_password_protected BOOLEAN DEFAULT FALSE,
    room_password VARCHAR(255),
    allow_guest_post BOOLEAN DEFAULT TRUE,
    slow_mode_seconds INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Chatroom members
CREATE TABLE IF NOT EXISTS chatroom_members (
    id BIGSERIAL PRIMARY KEY,
    chatroom_id BIGINT NOT NULL REFERENCES chatrooms(id) ON DELETE CASCADE,
    username VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_seen_at TIMESTAMP,
    is_online BOOLEAN DEFAULT FALSE,
    message_count BIGINT DEFAULT 0,
    UNIQUE (chatroom_id, username)
);

-- Chatroom bans with expiry tracking
CREATE TABLE IF NOT EXISTS chatroom_bans (
    id BIGSERIAL PRIMARY KEY,
    chatroom_id BIGINT NOT NULL REFERENCES chatrooms(id) ON DELETE CASCADE,
    banned_username VARCHAR(50) NOT NULL,
    banned_by_username VARCHAR(50) NOT NULL,
    reason TEXT,
    banned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    is_permanent BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE
);

-- Chatroom mutes with expiry tracking
CREATE TABLE IF NOT EXISTS chatroom_mutes (
    id BIGSERIAL PRIMARY KEY,
    chatroom_id BIGINT NOT NULL REFERENCES chatrooms(id) ON DELETE CASCADE,
    muted_username VARCHAR(50) NOT NULL,
    muted_by_username VARCHAR(50) NOT NULL,
    reason TEXT,
    muted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    is_permanent BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_chatrooms_room_name ON chatrooms (room_name);
CREATE INDEX IF NOT EXISTS idx_chatrooms_owner ON chatrooms (owner_username);
CREATE INDEX IF NOT EXISTS idx_chatrooms_public ON chatrooms (is_public, is_active, is_deleted);
CREATE INDEX IF NOT EXISTS idx_chatroom_members_chatroom ON chatroom_members (chatroom_id);
CREATE INDEX IF NOT EXISTS idx_chatroom_members_username ON chatroom_members (username);
CREATE INDEX IF NOT EXISTS idx_chatroom_bans_active ON chatroom_bans (chatroom_id, banned_username, is_active);
CREATE INDEX IF NOT EXISTS idx_chatroom_bans_expires ON chatroom_bans (expires_at) WHERE is_active = TRUE;
CREATE INDEX IF NOT EXISTS idx_chatroom_mutes_active ON chatroom_mutes (chatroom_id, muted_username, is_active);
CREATE INDEX IF NOT EXISTS idx_chatroom_mutes_expires ON chatroom_mutes (expires_at) WHERE is_active = TRUE;
