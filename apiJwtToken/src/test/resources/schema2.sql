-- Drop existing tables if they exist
DROP TABLE IF EXISTS user_application_roles; -- Table with FK to roles, applications, users
DROP TABLE IF EXISTS user_roles; -- Table with FK to users, roles
DROP TABLE IF EXISTS application_roles; -- Table with FK to applications, roles
DROP TABLE IF EXISTS user_applications; -- Table with FK to users, applications
DROP TABLE IF EXISTS roles; -- Referenced by user_application_roles, user_roles, application_roles
DROP TABLE IF EXISTS applications; -- Referenced by user_application_roles, application_roles, user_applications
DROP TABLE IF EXISTS app_users; -- Referenced by user_application_roles, user_roles, user_applications

-- Create app_users table (Users)
CREATE TABLE app_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
);

-- Create applications table
CREATE TABLE applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_name VARCHAR(255) NOT NULL,
    secret_key VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
);

ALTER TABLE applications
ADD COLUMN jwt_expiration BIGINT DEFAULT 3600000;

-- Index on application_name for faster lookups
CREATE INDEX idx_applications_application_name ON applications (application_name);

-- Create user_applications table (Many-to-Many between Users and Applications)
CREATE TABLE user_applications (
    user_id BIGINT,
    application_id BIGINT,
    PRIMARY KEY (user_id, application_id),
    FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE,
    FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE
);
ALTER TABLE user_applications
ADD COLUMN jwt_token VARCHAR(2048); -- Adjust VARCHAR length as needed

-- Optionally, add an index on the jwt_token column for faster lookups
CREATE INDEX idx_user_applications_jwt_token ON user_applications (jwt_token);
-- Indexes on user_applications
CREATE INDEX idx_user_applications_application_id ON user_applications (application_id);
CREATE INDEX idx_user_applications_user_id ON user_applications (user_id);

-- Create roles table
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
);

-- Create user_roles table (Many-to-Many between Users and Roles)
CREATE TABLE user_roles (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Indexes on user_roles
CREATE INDEX idx_user_roles_user_id ON user_roles (user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);

-- Create application_roles table (Many-to-Many between Applications and Roles)
CREATE TABLE application_roles (
    application_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (application_id, role_id),
    FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Indexes on application_roles
CREATE INDEX idx_application_roles_application_id ON application_roles (application_id);
CREATE INDEX idx_application_roles_role_id ON application_roles (role_id);

-- Create user_application_roles table (Many-to-Many with Roles for Users and Applications)
CREATE TABLE user_application_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    application_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE,
    FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Indexes on user_application_roles
CREATE INDEX idx_user_application_roles_user_id ON user_application_roles (user_id);
CREATE INDEX idx_user_application_roles_application_id ON user_application_roles (application_id);
CREATE INDEX idx_user_application_roles_role_id ON user_application_roles (role_id);

-- Insert into Applications
INSERT INTO applications (application_name, secret_key) VALUES 
('TestApp1', 'secret1'),
('TestApp2', 'secret2'),
('TestApp3', 'secret3');

-- Insert into Roles
INSERT INTO roles (role_name) VALUES 
('Role1'),
('Role2'),
('Role3');

-- Insert into Users
INSERT INTO app_users (username, password, email, created_at, updated_at) VALUES 
('user1', 'pass1', 'user1@example.com', NOW(), NOW()),
('user2', 'pass2', 'user2@example.com', NOW(), NOW());

-- Now insert into the join tables

-- user_application_roles: Assign users roles in applications
INSERT INTO user_application_roles (user_id, application_id, role_id) VALUES 
(1, 1, 1), 
(1, 1, 2), 
(1, 2, 2), 
(1, 2, 3), 
(2, 2, 2), 
(2, 3, 2);

-- user_roles: Assign roles to users
INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1), 
(1, 2), 
(2, 2);

-- application_roles: Assign roles to applications
INSERT INTO application_roles (application_id, role_id) VALUES 
(1, 1), 
(1, 2), 
(2, 2), 
(2, 3), 
(3, 1);
