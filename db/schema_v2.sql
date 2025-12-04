-- Database Schema V2 for Lost & Found System
CREATE DATABASE IF NOT EXISTS lostfound_db;
USE lostfound_db;

-- Disable foreign key checks for clean drop
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS claims;
DROP TABLE IF EXISTS matches;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS locations;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Users Table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Plain text for this assignment as requested, but hashing is better
    email VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'user', -- 'user' or 'admin'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Categories Table
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 3. Locations Table
CREATE TABLE locations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- 4. Items Table
CREATE TABLE items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    item_type VARCHAR(10) NOT NULL, -- 'LOST' or 'FOUND'
    item_name VARCHAR(100) NOT NULL,
    description TEXT,
    category_id INT,
    location_id INT,
    lost_found_date DATE,
    status VARCHAR(20) DEFAULT 'PENDING_APPROVAL', -- PENDING_APPROVAL, LISTED, RESOLVED, REJECTED
    contact_info VARCHAR(255),
    image_url VARCHAR(255), -- Optional: for future use
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE SET NULL
);

-- 5. Matches Table (For potential matches between Lost and Found items)
CREATE TABLE matches (
    id INT AUTO_INCREMENT PRIMARY KEY,
    lost_item_id INT NOT NULL,
    found_item_id INT NOT NULL,
    match_score INT DEFAULT 0, -- Simple score (e.g., 100 for exact match)
    status VARCHAR(20) DEFAULT 'POTENTIAL', -- POTENTIAL, CONFIRMED, REJECTED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (lost_item_id) REFERENCES items(id) ON DELETE CASCADE,
    FOREIGN KEY (found_item_id) REFERENCES items(id) ON DELETE CASCADE
);

-- 6. Claims Table (For users claiming found items)
CREATE TABLE claims (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    user_id INT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    claim_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- SEED DATA

-- Users
INSERT INTO users (username, password, email, role) VALUES 
('admin', 'admin123', 'admin@lostfound.com', 'admin'),
('john_doe', 'password123', 'john@example.com', 'user'),
('jane_smith', 'password123', 'jane@example.com', 'user');

-- Categories
INSERT INTO categories (name) VALUES 
('Electronics'),
('Accessories'),
('Documents'),
('Clothing'),
('Keys'),
('Others');

-- Locations
INSERT INTO locations (name) VALUES 
('Library'),
('Cafeteria'),
('Main Building'),
('Sports Complex'),
('Parking Lot'),
('Auditorium');

-- Items (Sample)
-- John lost his wallet in the Library
INSERT INTO items (user_id, item_type, item_name, description, category_id, location_id, lost_found_date, status, contact_info) 
VALUES 
((SELECT id FROM users WHERE username='john_doe'), 'LOST', 'Black Leather Wallet', 'Contains ID and cards', 
 (SELECT id FROM categories WHERE name='Accessories'), 
 (SELECT id FROM locations WHERE name='Library'), 
 CURDATE(), 'LISTED', 'john@example.com');

-- Jane found a wallet in the Library (Potential Match)
INSERT INTO items (user_id, item_type, item_name, description, category_id, location_id, lost_found_date, status, contact_info) 
VALUES 
((SELECT id FROM users WHERE username='jane_smith'), 'FOUND', 'Black Wallet', 'Found near entrance', 
 (SELECT id FROM categories WHERE name='Accessories'), 
 (SELECT id FROM locations WHERE name='Library'), 
 CURDATE(), 'PENDING_APPROVAL', 'jane@example.com');
