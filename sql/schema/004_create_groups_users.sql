-- Groups table
CREATE TABLE IF NOT EXISTS groups (
    id BIGSERIAL PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL,
    description TEXT,
    owner_username VARCHAR(50) NOT NULL,
    avatar_url VARCHAR(512),
    is_public BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    max_members INT DEFAULT 500,
    current_members INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Group members
CREATE TABLE IF NOT EXISTS group_members (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    username VARCHAR(50) NOT NULL,
    role VARCHAR(20) DEFAULT 'MEMBER',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (group_id, username)
);

-- Friendships
CREATE TABLE IF NOT EXISTS friendships (
    id BIGSERIAL PRIMARY KEY,
    user_username VARCHAR(50) NOT NULL,
    friend_username VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_username, friend_username)
);

-- Blocked users
CREATE TABLE IF NOT EXISTS blocked_users (
    id BIGSERIAL PRIMARY KEY,
    blocker_username VARCHAR(50) NOT NULL,
    blocked_username VARCHAR(50) NOT NULL,
    blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (blocker_username, blocked_username)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_groups_owner ON groups (owner_username);
CREATE INDEX IF NOT EXISTS idx_group_members_group_id ON group_members (group_id);
CREATE INDEX IF NOT EXISTS idx_group_members_username ON group_members (username);
CREATE INDEX IF NOT EXISTS idx_friendships_user ON friendships (user_username);
CREATE INDEX IF NOT EXISTS idx_friendships_friend ON friendships (friend_username);
CREATE INDEX IF NOT EXISTS idx_blocked_users_blocker ON blocked_users (blocker_username);
