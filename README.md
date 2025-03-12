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

    * **`POST /api/auth/register`:** Register a new user.
        * Request body: `{ "username": "...", "email": "...", "password": "..." }`
    * **`POST /api/auth/login`:** Login and get a JWT.
        * Request body: `{ "username": "...", "password": "..." }`
        * Response body: `{ "token": "...", "refreshToken": "..." }` (Refresh token optional)
    * **`POST /api/auth/refreshtoken`:** Refresh a JWT using a refresh token.(Optional)
        * Request body: `{ "refreshToken": "..." }`
        * Response body: `{ "token": "..." }`
    * **`GET /api/test/user`:** Protected endpoint (requires user role).
    * **`GET /api/test/admin`:** Protected endpoint (requires admin role).
    * **`GET /api/test/all`:** Public endpoint (no authentication required).

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

This project utilizes the following dependencies, managed by Maven:

* **Spring Boot Starters:**
    * `spring-boot-starter-data-jpa`: For JPA-based data access.
    * `spring-boot-starter-security`: For Spring Security integration.
    * `spring-boot-starter-web`: For building web applications.
    * `spring-boot-starter-validation`: For request body validation.
    * `spring-boot-starter-data-redis`: For Redis integration.
    * `spring-boot-starter-test`: For unit and integration testing.

* **Database Drivers:**
    * `com.h2database:h2`: For in-memory H2 database (runtime scope).
    * `com.mysql:mysql-connector-j`: For MySQL database connectivity.

* **Lombok:**
    * `org.projectlombok:lombok`: For reducing boilerplate code (optional).

* **JSON Web Token (JWT):**
    * `io.jsonwebtoken:jjwt-api`: JWT API.
    * `io.jsonwebtoken:jjwt-impl`: JWT implementation (runtime scope).
    * `io.jsonwebtoken:jjwt-jackson`: JWT Jackson integration (runtime scope).

* **Jakarta Validation:**
    * `jakarta.validation:jakarta.validation-api`: For validation annotations.

* **Jakarta Servlet API:**
    * `jakarta.servlet:jakarta.servlet-api`: For servlet functionality (provided scope).

* **Jakarta Persistence API:**
    * `jakarta.persistence:jakarta.persistence-api`: For JPA entity management.

* **Spring Security Test:**
    * `org.springframework.security:spring-security-test`: For Spring Security testing.

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