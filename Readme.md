# Book Exchange API

A backend REST API for a book exchange and gifting platform.

The application allows users to register, add books, share books with other users, give books permanently, return borrowed books, and manage books through admin endpoints.

This project was originally created as a university course project and is being improved as a portfolio project for Java backend development.

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
- Maven or Maven Wrapper
- Docker
- Docker Compose

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

## Run the Application Locally

After the database is running, start the Spring Boot application.

For Linux/macOS:

```bash
./mvnw clean spring-boot:run
```

For Windows:

```bash
mvnw.cmd clean spring-boot:run
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

Run tests with:

For Linux/macOS:

```bash
./mvnw test
```

For Windows:

```bash
mvnw.cmd test
```

---

## Current Status

The project is functional as a university backend project and is currently being improved for portfolio use.

Planned improvements:

- Improve README with real API examples
- Add more unit and integration tests
- Add DTO mapping layer where needed
- Improve global exception handling
- Add database migrations with Flyway or Liquibase
- Add GitHub Actions CI pipeline
- Improve Docker Compose to run both the database and application together
- Add sample Postman collection

---

## Author

Vladyslav Spyrydonov

GitHub: https://github.com/vladwpnz
