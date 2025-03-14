# Work in progress 

work in progress main error at the moment 
```
2025-03-11 15:16:03 2025-03-11 15:16:03.229 ERROR c.e.a.controller.LogoutController - Error invalidating application-specific token for TestApp1: Unable to connect to Redis
```

# Spring Boot JWT Authentication API

This project demonstrates a simple Spring Boot REST API secured with JSON Web Tokens (JWT). It allows user registration, login, and access to protected resources.

## Features

* **User Registration:** Allows new users to create an account.
* **User Login:** Generates a JWT upon successful login.
* **JWT Authentication:** Protects API endpoints using JWT authentication.
* **Role-Based Authorization (Optional):** Demonstrates how to implement role-based access control.
* **Refresh Token (Optional):** Shows how to implement a refresh token mechanism.
* **Basic Error Handling:** Provides basic error handling for authentication and authorization.

## Prerequisites

* Java 17+
* Maven 
* A database MySQL
* For test H2

## Role Action Table

| Role          | Application          | Action                                  | Description                                                     |
|---------------|----------------------|-----------------------------------------|-----------------------------------------------------------------|
| Admin         | System/Global        | Manage Users                            | Full control of Users.                                          |
| User          | System/Global        | List Users                              | Ability to list Users.                                          |
| Admin         | System/Global        | Manage Applications                     | Full control of Applications.                                   |
| User          | System/Global        | List Applications                       | Ability to list Applications.                                   |
| Admin         | System/Global        | Manage Roles                            | Full control of Roles.                                          |
| User          | System/Global        | List Roles                              | Ability to list Roles.                                          |
| Admin         | Application-Specific | Manage Users                            | Full control of Users in a specific application.                |
| User          | Application-Specific | List Users                              | Ability to list Users in a specific application.                |
| Admin         | Application-Specific | Manage Roles                            | Full control of Roles in a specific application.                |
| User          | Application-Specific | List Roles                              | Ability to list Roles in a specific application.                |


## Getting Started

1.  **Clone the repository:**

    ```bash
    git clone <repository_url>
    cd spring-boot-jwt-api
    ```

2.  **Configure the database:**

    * Update the `application.properties` or `application.yml` file with your database connection details.
    * Example using H2 in memory database:

    ```properties
    spring.datasource.url=jdbc:h2:mem:mydb
    spring.datasource.username=sa
    spring.datasource.password=password
    spring.jpa.hibernate.ddl-auto=create-drop
    spring.h2.console.enabled=true
    spring.h2.console.path=/h2-console
    ```

3.  **Configure JWT settings:**

    * Set the JWT secret key, expiration time, and other relevant properties in your `application.properties` or `application.yml` file.

    ```properties
    jwt.secret=your-secret-key
    jwt.expirationMs=86400000 # 24 hours
    jwt.refreshExpirationMs=604800000 # 7 days (Optional)
    ```

4.  **Build and run the application:**

    * Using Maven:

    ```bash
    ./mvnw spring-boot:run
    ```

    * Using Gradle:

    ```bash
    ./gradlew bootRun
    ```

5.  **API Endpoints:**

    * **`POST  /api/auth/register`:**
    * **`GET  /api/app/names`:**
    * **`GET  /api/app/expiration/{id}`:**
    * **`POST  /api/auth/{applicationName}/login`:**
    * **`GET  /api/endpoints`:**
    * **`GET  /api/auth/{applicationName}/roles/names`:**
    * **`GET  /api/app/roles/{applicationId}`:**
    * **`GET  /api/app/show/{id}`:**
    * **`GET  /api/app/index`:**
    * **`GET  /api/auth/logout`:**
    * **`POST  /api/auth/login`:**
    * **`DELETE  /api/roles/{id}`:**
    * **`GET  /api/roles/names`:**
    * **`  /error`:**
    * **`POST  /api/auth/{applicationName}/register`:**
    * **`DELETE  /api/app/{id}`:**
    * **`GET  /api/auth/{applicationName}/roles/{id}`:**
    * **`GET  /api/auth/users/index`:**
    * **`GET  /api/app/users/{applicationId}`:**
    * **`PUT  /api/roles/{id}`:**
    * **`GET  /api/app/name/{name}`:**
    * **`POST  /api/auth/{applicationName}/roles/`:**
    * **`POST  /api/auth/{applicationName}/register/{roleName}`:**
    * **`PUT  /api/app/{id}`:**
    * **`GET  /api/roles/index`:**
    * **`GET  /api/auth/{applicationName}/users/index`:**
    * **`GET  /api/app/secret/{id}`:**
    * **`GET  /api/roles/{id}`:**
    * **`POST  /api/roles/`:**
    * **`GET  /api/roles/application/{applicationId}`:**
    * **`GET  /api/auth/{applicationName}/roles/index`:**
    * **`DELETE  /api/auth/{applicationName}/roles/{id}`:**
    * **`POST  /api/app/`:**
    * **`PUT  /api/auth/{applicationName}/roles/{id}`:**
    * **`  /error`:**
    * **`GET  /api/auth/{applicationName}/logout`:**

## Technologies Used

* **Spring Boot:** 3.2.3 (via `spring-boot-starter-parent`)
* **Spring Security:** For authentication and authorization.
* **Spring Data JPA:** For database interaction and object-relational mapping.
* **H2 Database:** (Runtime, for development/testing)
* **MySQL Connector/J:** 8.0.33 (for MySQL database connectivity)
* **Lombok:** For reducing boilerplate code.
* **JSON Web Token (JWT):**
    * `jjwt-api`: 0.11.5
    * `jjwt-impl`: 0.11.5 (Runtime)
    * `jjwt-jackson`: 0.11.5 (Runtime)
* **Spring Validation:** For request body validation.
* **Jakarta Validation API:** 3.0.2
* **Jakarta Servlet API:** (Provided scope)
* **Spring Data Redis:** For Redis integration.
* **Spring Boot Test:** For unit and integration testing.
* **Spring Security Test:** For testing Spring Security components.
* **Jakarta Persistence API:** 3.1.0 (For JPA entities)
* **Maven:** For build automation.
* **Java:** 17

## Dependencies

This project uses several dependencies for functionality such as JWT authentication, database integration, and security. Below is a list of the key dependencies:

### Spring Boot Dependencies
- **spring-boot-starter-data-jpa**: Provides Spring Data JPA for database access.
- **spring-boot-starter-security**: Adds Spring Security for authentication and authorization.
- **spring-boot-starter-web**: Includes components for building web applications, including REST APIs.
- **spring-boot-starter-validation**: Adds validation capabilities to the application.
- **spring-boot-starter-data-redis**: Enables Redis support for the application.

### Database and Data Processing
- **h2**: H2 database, used for in-memory testing.
- **mysql-connector-j**: MySQL connector for database connection.

### JWT Dependencies
- **jjwt-api**: JJWT API for creating and parsing JSON Web Tokens.
- **jjwt-impl**: Implementation of JJWT used at runtime.
- **jjwt-jackson**: JJWT module for Jackson integration.

### Utility Dependencies
- **lombok**: Provides annotations to reduce boilerplate code (like getters, setters, etc.). This dependency is optional.
- **jackson-databind**: Jackson library for serializing and deserializing Java objects to and from JSON.

### Testing Dependencies
- **spring-boot-starter-test**: Provides testing tools for Spring Boot applications.
- **spring-security-test**: Provides security-specific test support for Spring Security.

### Build Plugins
- **spring-boot-maven-plugin**: Plugin for building Spring Boot applications with Maven.
- **maven-compiler-plugin**: Configures the Java compiler version.


## Example usage with curl:

1.  **Register a user:**

    ```bash
    curl -X POST -H "Content-Type: application/json" -d '{"username": "testuser", "email": "[email address removed]", "password": "password123"}' http://localhost:8080/api/auth/register
    ```

2.  **Login and get a JWT:**

    ```bash
    curl -X POST -H "Content-Type: application/json" -d '{"username": "testuser", "password": "password123"}' http://localhost:8080/api/auth/login
    ```

    * Copy the `token` value from the response.

3.  **Access a protected endpoint:**

    ```bash
    curl -X GET -H "Authorization: Bearer <your_jwt_token>" http://localhost:8080/api/test/user
    ```

## Optional Enhancements

* Implement proper password hashing (e.g., BCrypt).
* Add more comprehensive error handling and validation.
* Implement role-based authorization using Spring Security's `@PreAuthorize` annotations.
* Add unit and integration tests.
* Implement refresh token rotation for enhanced security.
* Use a proper key management solution for the JWT secret.
* Document the api using swagger or openapi.

## Contributing

Feel free to contribute to this project by submitting pull requests or opening issues.

## Useful commands

```
docker exec -it java_work /bin/bash
mvn clean install -DskipTests   
mvn spring-boot:run -Ddebug
```


## License

[MIT License](LICENSE) (or your preferred license)