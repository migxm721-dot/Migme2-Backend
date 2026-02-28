-- Store items table
CREATE TABLE IF NOT EXISTS store_items (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    type VARCHAR(50),
    category_id INT,
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    image_url VARCHAR(512),
    hotkey VARCHAR(50),
    required_level INT DEFAULT 0,
    popularity INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS purchases (
    id BIGSERIAL PRIMARY KEY,
    buyer_id VARCHAR(50) NOT NULL,
    item_id BIGINT NOT NULL,
    cost DECIMAL(10, 2) NOT NULL,
    quantity INT DEFAULT 1,
    recipients TEXT,
    message TEXT,
    is_private BOOLEAN DEFAULT FALSE,
    post_to_miniblog BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'completed',
    purchased_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS credits (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(50) UNIQUE NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    total_earned DECIMAL(15, 2) DEFAULT 0.00,
    total_spent DECIMAL(15, 2) DEFAULT 0.00,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(30),
    amount DECIMAL(15, 2) NOT NULL,
    from_user_id VARCHAR(50),
    to_user_id VARCHAR(50),
    description TEXT,
    payment_method VARCHAR(50),
    status VARCHAR(20) DEFAULT 'completed',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_store_items_type ON store_items (type);
CREATE INDEX IF NOT EXISTS idx_store_items_featured ON store_items (is_featured, is_active);
CREATE INDEX IF NOT EXISTS idx_purchases_buyer ON purchases (buyer_id);
CREATE INDEX IF NOT EXISTS idx_credits_user_id ON credits (user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_from_user ON transactions (from_user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_to_user ON transactions (to_user_id);
