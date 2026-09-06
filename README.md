# Autosalon Microservices

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-events-orange)
![gRPC](https://img.shields.io/badge/gRPC-sync%20API-blueviolet)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)

> Russian version: [README.ru.md](README.ru.md)

Autosalon Microservices is an educational Spring Boot project for a car dealership system. The project demonstrates a gradual transition from a modular backend to a microservice architecture with separate databases, asynchronous messaging, synchronous gRPC communication, authentication, migrations, and tests.

## Overview

The system consists of two backend services:

| Service | Responsibility |
|---|---|
| `order-service` | Users, car configurations, test drives, stock orders, custom orders, payment flow, outbox events, and gRPC client calls |
| `storage-service` | Cars, spare parts, assembly orders, RabbitMQ event processing, and gRPC inventory API |

Shared gRPC contracts are stored in:

```text
proto/
```

## Architecture

```text
Client / Postman / curl
        |
        v
order-service :8082
        |
        | gRPC request for available cars
        v
storage-service :9091
        |
        v
storage PostgreSQL :5433

order-service
        |
        | RabbitMQ event: order paid
        v
storage-service
        |
        v
assembly order processing
```

## Domain-Driven Structure

Both services follow a layered structure:

```text
domain/          business models, enums, events, repository interfaces
application/     use cases and application services
infrastructure/  REST controllers, JPA adapters, gRPC, RabbitMQ, security, exceptions
```

The main idea is to keep business logic independent from infrastructure details. REST, JPA, RabbitMQ, gRPC, and security are placed in the infrastructure layer.

## Tech Stack

- Java 21
- Spring Boot 3.2.5
- Spring Web
- Spring Data JPA
- Spring Security OAuth2 Resource Server
- PostgreSQL
- Liquibase
- RabbitMQ
- gRPC
- Keycloak
- Docker Compose
- Gradle
- JUnit 5
- Testcontainers

## Communication

### RabbitMQ

RabbitMQ is used for asynchronous order approval processing.

```text
order-service: order is paid
order-service: creates outbox event
order-service: publishes event to RabbitMQ
storage-service: consumes event
storage-service: creates or updates assembly order
```

### gRPC

gRPC is used for synchronous service-to-service requests when an immediate response is required.

```text
GET /api/v1/cars
-> order-service REST controller
-> order-service gRPC client
-> storage-service gRPC server
-> storage database
-> response with available cars
```

## Services And Ports

| Component | Port |
|---|---:|
| order-service | 8082 |
| storage-service HTTP | 8083 |
| storage-service gRPC | 9091 |
| Keycloak | 8081 |
| RabbitMQ | 5672 |
| RabbitMQ Management UI | 15672 |
| order PostgreSQL | 5432 |
| storage PostgreSQL | 5433 |

## Run Locally

Start infrastructure:

```bash
docker compose up -d order-postgres storage-postgres rabbitmq keycloak
```

Use Java 21:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
java -version
```

Start `storage-service` first:

```bash
./gradlew :storage-service:bootRun
```

Start `order-service` in another terminal:

```bash
./gradlew :order-service:bootRun
```

## API Example

Get a user token:

```bash
export USER_TOKEN=$(curl -s -X POST "http://localhost:8081/realms/autosalon/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=autosalon-api" \
  -d "username=user1" \
  -d "password=123456" \
  | sed -n 's/.*"access_token":"\([^"]*\)".*/\1/p')
```

Get available cars:

```bash
curl -i "http://localhost:8082/api/v1/cars" \
  -H "Authorization: Bearer $USER_TOKEN"
```

Get available car by id:

```bash
curl -i "http://localhost:8082/api/v1/cars/33333333-3333-3333-3333-333333333333" \
  -H "Authorization: Bearer $USER_TOKEN"
```

## RabbitMQ UI

```text
http://localhost:15672
```

Development credentials:

```text
username: guest
password: guest
```

## Keycloak

```text
http://localhost:8081
```

Development admin credentials:

```text
username: admin
password: admin
```

## Tests

Run tests:

```bash
./gradlew :order-service:test :storage-service:test
```

Compile both services:

```bash
./gradlew :order-service:classes :storage-service:classes
```

## Implemented Features

- Car catalog and filtering
- Spare parts management
- Car configuration validation
- Custom orders
- Stock orders
- Test drive requests
- JWT authentication with Keycloak
- Role-based access checks
- Separate PostgreSQL databases per service
- Liquibase migrations and seed data
- RabbitMQ event publishing and consuming
- Outbox pattern in `order-service`
- Assembly order processing in `storage-service`
- Idempotent event processing
- gRPC inventory API from `storage-service`
- REST endpoints in `order-service` backed by gRPC calls
- HTTP 503 handling when the gRPC storage service is unavailable

