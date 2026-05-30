# Book Exchange API

[![API Tests](https://github.com/vladwpnz/book-exchange-api-tests/actions/workflows/api-tests.yml/badge.svg)](https://github.com/vladwpnz/book-exchange-api-tests/actions/workflows/api-tests.yml)

A backend REST API for a book exchange and gifting platform.

The application allows users to register, add books, share books with other users, give books permanently, return borrowed books, and manage books through admin endpoints.

This project was originally created as a university course project and is being improved as a portfolio project for Java backend development and API testing.

---

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Hibernate
- Spring Security
- Bean Validation
- MySQL
- Maven
- Docker
- Docker Compose
- Swagger / OpenAPI
- JUnit 5
- Lombok

---

## Main Features

- User registration
- Basic authentication with Spring Security
- Role-based access control
- Add books to the system
- View books owned by the current user
- View books currently held by the current user
- Share a book with another user
- Give a book to another user
- Return a borrowed book
- Admin endpoint for viewing all books
- Admin endpoint for deleting books
- Admin endpoint for forcing book return
- Request validation
- API documentation with Swagger/OpenAPI
- MySQL database integration
- Docker Compose setup for MySQL and phpMyAdmin
- External automated API test suite in a separate repository

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/register` | Register a new user |
| POST | `/book/add` | Add a new book |
| GET | `/held` | Get books currently held by the authenticated user |
| GET | `/owned` | Get books owned by the authenticated user |
| POST | `/book/share` | Share a book with another user |
| POST | `/book/give` | Give a book to another user |
| POST | `/book/return` | Return a borrowed book |
| GET | `/items` | Get all books, admin access required |
| DELETE | `/book/delete?id={id}` | Delete a book, admin access required |
| POST | `/book/return/force?id={id}` | Force book return, admin access required |

---

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.friends.sharing
│   └── resources
└── test
```

---

## Prerequisites

Before running the project, make sure you have installed:

- Java 17+
- Maven
- Docker
- Docker Compose

---

## Database Configuration

The application uses MySQL.

Default local configuration:

```text
Database: friendssharing
Host: localhost
Port: 3306
Username: root
Password: empty
```

The database connection is configured in:

```text
src/main/resources/application.properties
```

---

## Run the Database with Docker Compose

The current Docker Compose configuration starts MySQL and phpMyAdmin.

```bash
docker compose up -d
```

Services:

- MySQL: `localhost:3306`
- Database name: `friendssharing`
- Username: `root`
- Password: empty
- phpMyAdmin: `http://localhost:9000`

---

## Alternative: Run MySQL with XAMPP on Windows

If you use XAMPP instead of Docker:

1. Open XAMPP Control Panel.
2. Start MySQL.
3. Make sure MySQL is running on port `3306`.
4. Create the required database if it does not exist:

```powershell
& "C:\xampp\mysql\bin\mysql.exe" -h 127.0.0.1 -P 3306 -u root -e "CREATE DATABASE IF NOT EXISTS friendssharing;"
```

Check databases:

```powershell
& "C:\xampp\mysql\bin\mysql.exe" -h 127.0.0.1 -P 3306 -u root -e "SHOW DATABASES;"
```

---

## Run the Application Locally

After the database is running, start the Spring Boot application.

```bash
mvn clean spring-boot:run
```

The application should be available at:

```text
http://localhost:8080
```

---

## Swagger / OpenAPI

After starting the application, Swagger UI should be available at:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

---

## Testing

Run backend tests with:

```bash
mvn test
```

---

## Automated API Tests

A separate black-box API testing project is available here:

```text
https://github.com/vladwpnz/book-exchange-api-tests
```

The API test project covers the main API flows using:

- Java 17
- Maven
- JUnit 5
- RestAssured
- AssertJ
- Jackson
- Postman
- GitHub Actions

Current API test coverage includes:

- User registration
- Basic authentication checks
- Unauthorized access
- Validation errors
- Book creation and reading
- Book sharing
- Book giving
- Book returning
- Admin-only operations

To run the API tests locally, start this backend application first and then run:

```bash
cd ../book-exchange-api-tests
mvn test
```

The API tests are stored in a separate repository to keep the backend application and external API testing suite independent.

---

## Current Status

The project is functional as a university backend project and is currently being improved for portfolio use.

Completed portfolio improvements:

- Added clear README documentation
- Added Swagger/OpenAPI documentation
- Added MySQL and Docker Compose setup
- Added separate automated API testing project
- Added Postman collection in the API testing repository
- Added GitHub Actions workflow in the API testing repository

Planned improvements:

- Improve README with real API request and response examples
- Add more unit and integration tests inside the main backend project
- Improve global exception handling
- Add DTO mapping layer where needed
- Add database migrations with Flyway or Liquibase
- Improve Docker Compose to run both the database and application together
- Improve CI/CD pipeline for backend build and tests

---

## Related Repository

Automated API tests for this project:

```text
https://github.com/vladwpnz/book-exchange-api-tests
```

---

## Author

Vladyslav Spyrydonov

GitHub: https://github.com/vladwpnz
