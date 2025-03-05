DROP TABLE IF EXISTS user_application_roles;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS application_roles;
DROP TABLE IF EXISTS User_Applications;
DROP TABLE IF EXISTS Roles;
DROP TABLE IF EXISTS Applications;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
);

CREATE TABLE Applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_name VARCHAR(255) NOT NULL,
    secret_key VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
);

CREATE INDEX idx_applications_application_name ON Applications (application_name);

CREATE TABLE User_Applications (
    user_id BIGINT,
    application_id BIGINT,
    PRIMARY KEY (user_id, application_id),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (application_id) REFERENCES Applications(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_applications_application_id ON User_Applications (application_id);
CREATE INDEX idx_user_applications_user_id ON User_Applications (user_id);

CREATE TABLE Roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
);

CREATE TABLE user_roles (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES Roles(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_roles_user_id ON user_roles (user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);

CREATE TABLE application_roles (
    application_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (application_id, role_id),
    FOREIGN KEY (application_id) REFERENCES Applications(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES Roles(id) ON DELETE CASCADE
);

CREATE INDEX idx_application_roles_application_id ON Application_Roles (application_id);
CREATE INDEX idx_application_roles_role_id ON Application_Roles (role_id);

CREATE TABLE user_application_roles (
    user_id BIGINT NOT NULL,
    application_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, application_id, role_id),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (application_id) REFERENCES Applications(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES Roles(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_application_roles_user_id ON user_application_roles (user_id);
CREATE INDEX idx_user_application_roles_application_id ON user_application_roles (application_id);
CREATE INDEX idx_user_application_roles_role_id ON user_application_roles (role_id);

-- -- Insert into Applications first
-- INSERT INTO Applications (application_name, secret_key) VALUES ('TestApp1', 'secret1');
-- INSERT INTO Applications (application_name, secret_key) VALUES ('TestApp2', 'secret2');
-- INSERT INTO Applications (application_name, secret_key) VALUES ('TestApp3', 'secret3');

-- -- Insert into Roles
-- INSERT INTO Roles (role_name) VALUES ('Role1');
-- INSERT INTO Roles (role_name) VALUES ('Role2');
-- INSERT INTO Roles (role_name) VALUES ('Role3');

-- -- Insert into Users
-- INSERT INTO Users (username, password, email) VALUES ('user1', 'pass1', 'user1@example.com');
-- INSERT INTO Users (username, password, email) VALUES ('user2', 'pass2', 'user2@example.com');

-- -- Now insert into the join tables
-- INSERT INTO user_application_roles (user_id, application_id, role_id) VALUES (1, 1, 1), (1, 1, 2), (1, 2, 2), (1, 2, 3), (2, 2, 2), (2,3,2);