-- =====================================================================
-- Community Skill Finder - Database Schema & Seed Data
-- Target Database: MySQL 8.0+
-- Relational Model normalized up to 3NF with Integrity Constraints
-- =====================================================================

CREATE DATABASE IF NOT EXISTS community_skill_finder;
USE community_skill_finder;

-- Drop tables in reverse order of foreign key dependencies
DROP TABLE IF EXISTS partnerships;
DROP TABLE IF EXISTS user_learn_skills;
DROP TABLE IF EXISTS user_teach_skills;
DROP TABLE IF EXISTS skills;
DROP TABLE IF EXISTS users;

-- 1. Users Table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    bio TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Skills Table (Master Catalog)
CREATE TABLE skills (
    skill_id INT AUTO_INCREMENT PRIMARY KEY,
    skill_name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL
) ENGINE=InnoDB;

-- 3. User Teach Skills (Junction Table: Users <-> Skills)
CREATE TABLE user_teach_skills (
    user_id INT NOT NULL,
    skill_id INT NOT NULL,
    exp_level VARCHAR(30) NOT NULL DEFAULT 'INTERMEDIATE',
    PRIMARY KEY (user_id, skill_id),
    CONSTRAINT fk_teach_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_teach_skill FOREIGN KEY (skill_id) REFERENCES skills(skill_id) ON DELETE CASCADE,
    CONSTRAINT chk_exp_level CHECK (exp_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT'))
) ENGINE=InnoDB;

-- 4. User Learn Skills (Junction Table: Users <-> Skills)
CREATE TABLE user_learn_skills (
    user_id INT NOT NULL,
    skill_id INT NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    PRIMARY KEY (user_id, skill_id),
    CONSTRAINT fk_learn_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_learn_skill FOREIGN KEY (skill_id) REFERENCES skills(skill_id) ON DELETE CASCADE,
    CONSTRAINT chk_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH'))
) ENGINE=InnoDB;

-- 5. Partnerships Table (Lifecycle Management)
CREATE TABLE partnerships (
    partnership_id INT AUTO_INCREMENT PRIMARY KEY,
    mentor_id INT NOT NULL,
    mentee_id INT NOT NULL,
    skill_id INT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_partner_mentor FOREIGN KEY (mentor_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_partner_mentee FOREIGN KEY (mentee_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_partner_skill FOREIGN KEY (skill_id) REFERENCES skills(skill_id) ON DELETE SET NULL,
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'COMPLETED')),
    CONSTRAINT chk_different_users CHECK (mentor_id <> mentee_id)
) ENGINE=InnoDB;

-- =====================================================================
-- Seed Data for Demonstration & Reciprocal Testing
-- =====================================================================

-- Sample Skills Catalog
INSERT INTO skills (skill_name, category) VALUES
('Java', 'Programming Languages'),
('Python', 'Programming Languages'),
('SQL', 'Databases'),
('Docker', 'DevOps & Cloud'),
('React', 'Frontend Web'),
('Spring Boot', 'Backend Frameworks'),
('Git', 'Tools & Version Control'),
('Machine Learning', 'Data Science & AI');

-- Sample Users (password_hash is a placeholder seed value; auth not yet implemented)
INSERT INTO users (name, email, password_hash, bio) VALUES
('Alice Johnson', 'alice@example.com', 'pbkdf2:sha256:210000:c2FsdA==:aGFzaA==', 'Backend enthusiast with deep Java experience. Keen to master SQL optimization.'),
('Bob Smith', 'bob@example.com', 'pbkdf2:sha256:210000:c2FsdA==:aGFzaA==', 'Database administrator wanting to level up backend application development with Java.'),
('Charlie Brown', 'charlie@example.com', 'pbkdf2:sha256:210000:c2FsdA==:aGFzaA==', 'Frontend dev learning Python for data analysis.'),
('Diana Prince', 'diana@example.com', 'pbkdf2:sha256:210000:c2FsdA==:aGFzaA==', 'Python and ML researcher eager to learn React for building dashboards.');

-- Teaching skills
-- Alice teaches Java (Bob wants this)
INSERT INTO user_teach_skills (user_id, skill_id, exp_level) VALUES
(1, 1, 'EXPERT'),      -- Alice teaches Java
(1, 6, 'ADVANCED');    -- Alice teaches Spring Boot

-- Bob teaches SQL (Alice wants this -> Reciprocal match with Alice!)
INSERT INTO user_teach_skills (user_id, skill_id, exp_level) VALUES
(2, 3, 'EXPERT'),      -- Bob teaches SQL
(2, 4, 'INTERMEDIATE');-- Bob teaches Docker

-- Charlie teaches React (Diana wants this)
INSERT INTO user_teach_skills (user_id, skill_id, exp_level) VALUES
(3, 5, 'ADVANCED');    -- Charlie teaches React

-- Diana teaches Python (Charlie wants this -> Reciprocal match with Charlie!)
INSERT INTO user_teach_skills (user_id, skill_id, exp_level) VALUES
(4, 2, 'EXPERT'),      -- Diana teaches Python
(4, 8, 'ADVANCED');    -- Diana teaches Machine Learning

-- Learning goals
-- Alice wants to learn SQL
INSERT INTO user_learn_skills (user_id, skill_id, priority) VALUES
(1, 3, 'HIGH'),        -- Alice wants SQL
(1, 4, 'LOW');         -- Alice wants Docker

-- Bob wants to learn Java
INSERT INTO user_learn_skills (user_id, skill_id, priority) VALUES
(2, 1, 'HIGH');        -- Bob wants Java

-- Charlie wants to learn Python
INSERT INTO user_learn_skills (user_id, skill_id, priority) VALUES
(3, 2, 'HIGH');        -- Charlie wants Python

-- Diana wants to learn React
INSERT INTO user_learn_skills (user_id, skill_id, priority) VALUES
(4, 5, 'HIGH');        -- Diana wants React
