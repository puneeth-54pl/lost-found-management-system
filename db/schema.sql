CREATE DATABASE IF NOT EXISTS lostfound_db;
USE lostfound_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- In a real app, hash this!
    email VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'user', -- 'user' or 'admin'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Items Table
CREATE TABLE IF NOT EXISTS items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    item_type VARCHAR(10) NOT NULL, -- 'LOST' or 'FOUND'
    item_name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50),
    location VARCHAR(100),
    lost_found_date DATE,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED, RETURNED
    contact_info VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Seed Admin User
-- Password is 'admin123'
INSERT INTO users (username, password, email, role) 
SELECT 'admin', 'admin123', 'admin@lostfound.com', 'admin'
WHERE NOT EXISTS (SELECT * FROM users WHERE username = 'admin');

-- Seed Sample Data
INSERT INTO users (username, password, email, role)
SELECT 'john_doe', 'password123', 'john@example.com', 'user'
WHERE NOT EXISTS (SELECT * FROM users WHERE username = 'john_doe');

INSERT INTO items (user_id, item_type, item_name, description, category, location, lost_found_date, status, contact_info)
SELECT id, 'LOST', 'Black Wallet', 'Leather wallet with ID cards', 'Accessories', 'Library', '2023-10-01', 'APPROVED', 'john@example.com'
FROM users WHERE username = 'john_doe'
LIMIT 1;
