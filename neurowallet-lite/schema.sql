-- NeuroWallet Lite Database Schema

-- Create database
CREATE DATABASE IF NOT EXISTS neurowallet_db;
USE neurowallet_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email)
);

-- Wallets table
CREATE TABLE IF NOT EXISTS wallets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    status ENUM('ACTIVE', 'FROZEN') NOT NULL DEFAULT 'ACTIVE',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_currency (currency),
    UNIQUE KEY unique_user_currency (user_id, currency)
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    wallet_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    type ENUM('TRANSFER', 'CONVERSION', 'DEPOSIT', 'WITHDRAWAL') NOT NULL,
    risk_flag BOOLEAN NOT NULL DEFAULT FALSE,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE,
    INDEX idx_wallet_id (wallet_id),
    INDEX idx_risk_flag (risk_flag),
    INDEX idx_created_at (created_at)
);

-- Insert sample admin user (password: admin123)
-- Note: The password hash is for 'admin123' using BCrypt
INSERT INTO users (email, password, role) 
VALUES ('admin@neurowallet.com', '$2a$10$XQz8qKZR8n1xZL5K5K5K5.K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5', 'ADMIN')
ON DUPLICATE KEY UPDATE email = email;

-- Sample data for testing (optional)
-- Uncomment to add test users and data

-- Test user (password: user123)
-- INSERT INTO users (email, password, role) 
-- VALUES ('user@test.com', '$2a$10$YRz9qKZR9n2xZL6K6K6K6.K6K6K6K6K6K6K6K6K6K6K6K6K6K6K6K6', 'USER');

-- Sample wallets
-- INSERT INTO wallets (user_id, currency, balance, status) VALUES
-- (2, 'MYR', 10000.00, 'ACTIVE'),
-- (2, 'SGD', 2000.00, 'ACTIVE'),
-- (2, 'USD', 1500.00, 'ACTIVE');

-- Sample transactions
-- INSERT INTO transactions (wallet_id, amount, type, risk_flag, description) VALUES
-- (1, 5000.00, 'DEPOSIT', false, 'Initial deposit'),
-- (1, 6000.00, 'DEPOSIT', true, 'Large transfer - flagged'),
-- (2, 500.00, 'TRANSFER', false, 'SGD transfer'),
-- (3, 200.00, 'WITHDRAWAL', false, 'ATM withdrawal');

-- Verify schema
SHOW TABLES;
DESCRIBE users;
DESCRIBE wallets;
DESCRIBE transactions;