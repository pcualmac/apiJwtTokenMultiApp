-- Drop existing tables if they exist
DROP TABLE IF EXISTS user_application_roles; -- Table with FK to roles, applications, users
DROP TABLE IF EXISTS user_roles; -- Table with FK to users, roles
DROP TABLE IF EXISTS application_roles; -- Table with FK to applications, roles
DROP TABLE IF EXISTS user_applications; -- Table with FK to users, applications
DROP TABLE IF EXISTS roles; -- Referenced by user_application_roles, user_roles, application_roles
DROP TABLE IF EXISTS applications; -- Referenced by user_application_roles, application_roles, user_applications
DROP TABLE IF EXISTS app_users; -- Referenced by user_application_roles, user_roles, user_applications