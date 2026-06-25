<div align="center">

# 📚 Book Exchange API

### Spring Boot REST API for managing physical book ownership and exchanges

The backend service for the Book Exchange full-stack platform.

[![API Tests](https://github.com/vladwpnz/book-exchange-api-tests/actions/workflows/api-tests.yml/badge.svg)](https://github.com/vladwpnz/book-exchange-api-tests/actions/workflows/api-tests.yml)
![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk\&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?logo=springboot\&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-Basic_Auth-6DB33F?logo=springsecurity\&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.4-4479A1?logo=mysql\&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker\&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?logo=apachemaven\&logoColor=white)

</div>

---

## Overview

Book Exchange API is a Spring Boot REST service for managing a shared catalog and the lifecycle of physical books.

The application separates general catalog metadata from individual physical copies. Every copy has an owner and may also have a temporary holder.

The backend supports:

* user registration;
* HTTP Basic authentication;
* personal profiles;
* shared catalog search;
* physical book copy management;
* temporary sharing;
* permanent ownership transfer;
* returning borrowed copies;
* administrator inventory operations;
* database migrations;
* OpenAPI documentation;
* automated backend and black-box API testing.

## Related Repositories

| Repository                                                                     | Purpose                                 |
| ------------------------------------------------------------------------------ | --------------------------------------- |
| [book-exchange-ui](https://github.com/vladwpnz/book-exchange-ui)               | React and TypeScript frontend           |
| [book-exchange-api](https://github.com/vladwpnz/book-exchange-api)             | Spring Boot REST API                    |
| [book-exchange-api-tests](https://github.com/vladwpnz/book-exchange-api-tests) | Independent RestAssured black-box tests |

---

## Domain Model

The application distinguishes between a catalog entry and a physical book copy.

### Catalog entry

Contains shared metadata:

* title;
* author;
* genre;
* description;
* cover information.

Multiple users may add their own physical copies from the same catalog entry.

### Physical copy

Represents a real book belonging to a user.

A physical copy tracks:

* its owner;
* its current holder;
* whether it is available;
* whether it is temporarily shared;
* whether ownership has been permanently transferred.

### Ownership and possession

The owner and the holder may be different users.

```text
Available book
Owner  ───────────────► Holder
Alice                    Alice
```

```text
Temporarily shared book
Owner  ───────────────► Holder
Alice                    Bob
```

```text
Permanently given book
Owner  ───────────────► Holder
Bob                      Bob
```

---

## Main Features

### Authentication and authorization

* Public user registration
* HTTP Basic authentication
* Protected REST endpoints
* User and administrator roles
* Role-based administrator operations
* Password encoding
* Strict invalid-credential handling
* No dedicated login endpoint

Authentication is verified by calling protected endpoints with the supplied Basic Auth credentials.

### User profiles

* Read the authenticated user profile
* Update profile information
* Store user name, email and avatar data
* Return ownership and holding statistics

### Shared catalog

* Browse catalog books
* Search by title or author
* Read an individual catalog entry
* Add a physical copy from the catalog
* Add a missing title manually
* Prevent duplicate catalog entries
* Seeded catalog containing 50 books

### Personal books

* Add physical copies
* View books owned by the authenticated user
* View books held by the authenticated user
* Track current ownership and possession
* Preserve catalog metadata

### Exchange workflows

#### Share

Temporarily move a physical copy to another user while keeping the original owner.

#### Give

Permanently transfer ownership and possession to another user.

#### Return

Move a temporarily held copy back to its owner.

### Administration

Administrators can:

* view the complete inventory;
* inspect owners and holders;
* delete books;
* force the return of borrowed copies.

### Validation and error handling

* Request validation with Bean Validation
* Duplicate resource checks
* Invalid operation protection
* Authentication and authorization responses
* Centralized API error responses
* OpenAPI response documentation

---

## Technology Stack

| Technology              | Purpose                           |
| ----------------------- | --------------------------------- |
| Java 17                 | Backend language                  |
| Spring Boot 3.2.5       | Application framework             |
| Spring Web              | REST controllers                  |
| Spring Security         | Authentication and authorization  |
| Spring Data JPA         | Repository abstraction            |
| Hibernate               | Object-relational mapping         |
| Bean Validation         | Request validation                |
| MySQL 8.4               | Relational database               |
| Flyway                  | Schema migrations and seed data   |
| Springdoc OpenAPI 2.5.0 | Swagger and OpenAPI documentation |
| Maven                   | Build and dependency management   |
| Docker Compose          | Local database environment        |
| Lombok                  | Boilerplate reduction             |
| JUnit 5                 | Backend tests                     |
| Spring Boot Test        | Integration tests                 |
| Spring Security Test    | Security tests                    |

---

## Architecture

```text
┌─────────────────────────────────────┐
│         Frontend / API client        │
└─────────────────┬───────────────────┘
                  │
                  │ HTTP Basic Auth + JSON
                  ▼
┌─────────────────────────────────────┐
│           REST controllers           │
│                                     │
│ Registration • Profile • Catalog    │
│ Books • Transfers • Administration  │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│            Service layer             │
│                                     │
│ Validation • Ownership rules        │
│ Transfer logic • Business errors    │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│          Spring Data JPA             │
│             Hibernate                │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│             MySQL 8.4                │
│                                     │
│ Users • Catalog • Physical copies   │
└─────────────────────────────────────┘
```

Spring Security processes authentication and authorization before protected controller methods are executed.

Flyway applies versioned database changes before Hibernate validates the resulting schema.

---

## API Overview

### Registration and profile

| Method  | Endpoint    | Access | Description                    |
| ------- | ----------- | ------ | ------------------------------ |
| `POST`  | `/register` | Public | Register a new user            |
| `GET`   | `/me`       | User   | Read the authenticated profile |
| `PATCH` | `/me`       | User   | Update profile information     |

### Catalog

| Method | Endpoint                         | Access | Description                          |
| ------ | -------------------------------- | ------ | ------------------------------------ |
| `GET`  | `/catalog/books`                 | User   | Browse catalog books                 |
| `GET`  | `/catalog/books?query={query}`   | User   | Search by title or author            |
| `GET`  | `/catalog/books/{id}`            | User   | Read one catalog entry               |
| `POST` | `/book/add/from-catalog?id={id}` | User   | Add a physical copy from the catalog |

### Personal books

| Method | Endpoint    | Access | Description               |
| ------ | ----------- | ------ | ------------------------- |
| `POST` | `/book/add` | User   | Add a book manually       |
| `GET`  | `/owned`    | User   | List owned books          |
| `GET`  | `/held`     | User   | List currently held books |

### Transfers

| Method | Endpoint       | Access | Description              |
| ------ | -------------- | ------ | ------------------------ |
| `POST` | `/book/share`  | User   | Temporarily share a book |
| `POST` | `/book/give`   | User   | Permanently give a book  |
| `POST` | `/book/return` | User   | Return a borrowed book   |

### Administration

| Method   | Endpoint                     | Access | Description                     |
| -------- | ---------------------------- | ------ | ------------------------------- |
| `GET`    | `/items`                     | Admin  | List the complete inventory     |
| `DELETE` | `/book/delete?id={id}`       | Admin  | Delete a book                   |
| `POST`   | `/book/return/force?id={id}` | Admin  | Force a borrowed book to return |

Request and response schemas are available through Swagger UI while the application is running.

---

## Database Configuration

The default Spring profile is:

```text
local
```

Default local database configuration:

```text
Database: friendssharing
Host: localhost
Port: 3306
Username: root
Password: empty
```

The local profile uses:

```properties
spring.jpa.hibernate.ddl-auto=validate
spring.sql.init.mode=never
spring.flyway.locations=classpath:db/migration,classpath:db/seed
```

This means:

* Flyway owns schema creation and updates;
* local seed data is applied through Flyway;
* Hibernate validates the schema;
* Hibernate does not create or drop tables automatically.

### Spring profiles

| Profile | Purpose                                                |
| ------- | ------------------------------------------------------ |
| `local` | Local development with schema migrations and seed data |
| `test`  | Automated backend tests using the test database        |

---

## Database Migrations

Flyway manages versioned schema and seed changes.

```text
src/main/resources
└── db
    ├── migration
    └── seed
```

Migration responsibilities include:

* creating application tables;
* maintaining constraints;
* evolving the data model;
* creating catalog structures;
* inserting local catalog data.

---

## Running Locally

### Requirements

Install:

* Java 17
* Maven
* Docker Desktop
* Git

### 1. Clone the repository

```bash
git clone https://github.com/vladwpnz/book-exchange-api.git
cd book-exchange-api
```

### 2. Configure Docker values

The repository contains `.env.example`.

Copy it when custom values are required:

```bash
cp .env.example .env
```

Default Docker variables:

```env
MYSQL_DATABASE=friendssharing
MYSQL_PORT=3306
MYSQL_ALLOW_EMPTY_PASSWORD=yes
```

### 3. Start MySQL

```bash
docker compose up -d
```

Docker Compose starts:

```text
Container: book-exchange-mysql
Image: mysql:8.4
Port: 3306
Database: friendssharing
Volume: mysql-data
```

Check its state:

```bash
docker compose ps
```

The database health check must report a healthy container before starting the API.

### 4. Start the application

```bash
mvn spring-boot:run
```

The API starts at:

```text
http://localhost:8080
```

### 5. Stop the database

Preserve local data:

```bash
docker compose down
```

Delete the volume only when a completely clean database is required:

```bash
docker compose down -v
```

---

## Swagger and OpenAPI

Start the backend and open:

### Swagger UI

```text
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI JSON

```text
http://localhost:8080/v3/api-docs
```

Swagger describes:

* available endpoints;
* authentication requirements;
* request bodies;
* response schemas;
* validation errors;
* administrator-only operations.

---

## Authentication

The API uses HTTP Basic authentication.

Example request:

```bash
curl -u user@example.com:password \
  http://localhost:8080/owned
```

There is no dedicated `/login` endpoint.

The frontend verifies credentials by sending an authenticated request to a protected resource.

---

## Backend Tests

Start MySQL first:

```bash
docker compose up -d
```

Then run:

```bash
mvn clean verify
```

Latest verified result:

```text
Tests run: 77
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

The backend suite covers:

* controllers;
* services;
* repositories;
* validation;
* security configuration;
* role restrictions;
* catalog behavior;
* duplicate protection;
* ownership rules;
* transfer workflows;
* exception handling.

Tests use the `test` Spring profile and a separate test database.

---

## Independent API Tests

Black-box HTTP tests are maintained separately:

[book-exchange-api-tests](https://github.com/vladwpnz/book-exchange-api-tests)

The external suite uses:

* Java 17;
* Maven;
* JUnit 5;
* RestAssured;
* AssertJ;
* Jackson;
* GitHub Actions.

Latest verified API test result:

```text
AdminApiTest: 10 passed
AuthApiTest: 7 passed
BookApiTest: 7 passed
BookTransferApiTest: 6 passed

Tests run: 30
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

The API tests validate the service only through its public HTTP contract.

---

## Continuous Integration

The separate API test workflow:

1. starts a MySQL service;
2. checks out this backend repository;
3. starts the Spring Boot API;
4. waits until `http://localhost:8080` is available;
5. runs all RestAssured tests;
6. uploads Maven Surefire reports.

This keeps the application and its external verification suite independent.

---

## Project Structure

```text
book-exchange-api
├── .env.example
├── docker-compose.yml
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.friends.sharing
│   │   └── resources
│   │       ├── application.properties
│   │       ├── application-local.properties
│   │       └── db
│   │           ├── migration
│   │           └── seed
│   └── test
│       ├── java
│       └── resources
│           └── application-test.properties
└── README.md
```

---

## Engineering Decisions

### HTTP Basic authentication

Basic Auth keeps the authentication flow simple and transparent for the current project scope.

### Separate catalog and physical copies

Catalog metadata is reusable, while each personal book represents a real physical copy with its own ownership state.

### Separate owner and holder

This allows temporary sharing without losing the original owner.

### Flyway-controlled schema

Database changes are reproducible and versioned instead of being generated automatically by Hibernate.

### Independent black-box tests

External API tests verify HTTP behavior without importing or depending on backend implementation classes.

### Dockerized local database

Docker Compose provides a repeatable MySQL environment while persisting data in a named volume.

---

## Verified Quality

* 77 passing backend tests
* 30 passing black-box API tests
* 0 test failures
* 0 test errors
* 0 skipped tests
* OpenAPI documentation
* Bean Validation
* Role-based authorization
* Flyway migrations
* Docker health check
* Separate local and test profiles
* Independent API test workflow

---

## Author

### Vladyslav Spyrydonov

GitHub: [@vladwpnz](https://github.com/vladwpnz)
