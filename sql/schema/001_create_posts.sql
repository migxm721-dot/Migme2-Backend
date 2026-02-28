-- Posts table for mini blog system
CREATE TABLE IF NOT EXISTS posts (
    id BIGSERIAL PRIMARY KEY,
    author_id VARCHAR(50) NOT NULL,
    author_username VARCHAR(50) NOT NULL,
    body TEXT,
    type SMALLINT NOT NULL DEFAULT 0,
    status VARCHAR(20) DEFAULT 'active',
    privacy SMALLINT NOT NULL DEFAULT 0,
    photo_url VARCHAR(512),
    video_url VARCHAR(512),
    link_url VARCHAR(512),
    location VARCHAR(255),
    tags TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    root_post_id BIGINT,
    parent_post_id BIGINT,
    reply_count INT DEFAULT 0,
    reshare_count INT DEFAULT 0,
    watch_count INT DEFAULT 0,
    is_watched BOOLEAN DEFAULT FALSE,
    is_locked BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    tag_id INT,
    group_id BIGINT
);

-- Post watchlist (favorites)
CREATE TABLE IF NOT EXISTS post_watchlist (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    post_id BIGINT NOT NULL,
    watched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, post_id)
);

-- Post mentions
CREATE TABLE IF NOT EXISTS post_mentions (
    id BIGSERIAL PRIMARY KEY,
    mentioned_user_id VARCHAR(50) NOT NULL,
    post_id BIGINT NOT NULL,
    mentioned_by_user_id VARCHAR(50) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Post hashtags
CREATE TABLE IF NOT EXISTS post_hashtags (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    hashtag VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for posts
CREATE INDEX IF NOT EXISTS idx_posts_author_id ON posts (author_id);
CREATE INDEX IF NOT EXISTS idx_posts_created_at ON posts (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_posts_parent_post_id ON posts (parent_post_id);
CREATE INDEX IF NOT EXISTS idx_posts_root_post_id ON posts (root_post_id);
CREATE INDEX IF NOT EXISTS idx_posts_group_id ON posts (group_id);
CREATE INDEX IF NOT EXISTS idx_post_watchlist_user_id ON post_watchlist (user_id);
CREATE INDEX IF NOT EXISTS idx_post_mentions_mentioned_user_id ON post_mentions (mentioned_user_id);
