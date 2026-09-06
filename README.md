# Autosalon Microservices

Spring Boot microservices project for a car dealership system. The application demonstrates a gradual transition from a monolithic structure to microservices with separate databases, asynchronous messaging, synchronous gRPC communication, security, migrations, and tests.

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

## Architecture

The project contains two main microservices:

- `order-service` - works with users, car configurations, test drives, stock orders, custom orders, payment flow, outbox events, and gRPC client calls to storage.
- `storage-service` - works with cars, spare parts, assembly orders, RabbitMQ event processing, and exposes a gRPC API for car inventory.

Shared gRPC contracts are stored in:

```text
proto/
```

Main modules:

```text
order-service/
storage-service/
proto/
docker-compose.yml
```

## Domain-Driven Structure

Each service follows a layered structure:

```text
domain/          business models, enums, events, repository interfaces
application/     use cases and application services
infrastructure/  REST controllers, JPA adapters, gRPC, RabbitMQ, security, exceptions
```

The domain and application layers are separated from infrastructure details. Persistence, messaging, and transport protocols are implemented in the infrastructure layer.

## Communication

### Asynchronous Communication

RabbitMQ is used for asynchronous order approval flow.

Example flow:

```text
order-service: order is paid
order-service: creates outbox event
order-service: publishes event to RabbitMQ
storage-service: consumes event
storage-service: creates or updates assembly order
```

### Synchronous Communication

gRPC is used when `order-service` needs an immediate response from `storage-service`.

Example flow:

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

## Running The Project

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

## RabbitMQ UI

Open:

```text
http://localhost:15672
```

Default development credentials:

```text
username: guest
password: guest
```

## Keycloak

Open:

```text
http://localhost:8081
```

Default development admin credentials:

```text
username: admin
password: admin
```

The project expects a Keycloak realm and client configured for JWT authentication.

## API Examples

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

Check token length:

```bash
echo ${#USER_TOKEN}
```

Get available cars through `order-service` and gRPC:

```bash
curl -i "http://localhost:8082/api/v1/cars" \
  -H "Authorization: Bearer $USER_TOKEN"
```

Get available car by id:

```bash
curl -i "http://localhost:8082/api/v1/cars/33333333-3333-3333-3333-333333333333" \
  -H "Authorization: Bearer $USER_TOKEN"
```

## Tests

Run unit and integration tests:

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
- JWT-based authorization with Keycloak
- Role-based access checks
- Separate PostgreSQL databases per service
- Liquibase migrations and seed data
- RabbitMQ event publishing and consuming
- Outbox pattern in `order-service`
- Assembly order processing in `storage-service`
- Idempotent event processing
- gRPC inventory API from `storage-service`
- REST endpoints in `order-service` backed by gRPC calls
- Error handling for unavailable gRPC service with HTTP 503

## Notes

This is an educational project. Credentials in Docker Compose and application configuration are development-only values and must not be used in production.
