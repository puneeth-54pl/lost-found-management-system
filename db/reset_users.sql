-- Reset Users and Clear Data
SET FOREIGN_KEY_CHECKS = 0;

-- Clear dependent tables first or just truncate users if cascades work, but truncate users might fail if fk checks are on.
TRUNCATE TABLE claims;
TRUNCATE TABLE matches;
TRUNCATE TABLE items;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;

-- Add Defaults
INSERT INTO users (username, password, email, role) VALUES 
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa82280d64d370d52', 'admin@lostfound.com', 'admin'),

