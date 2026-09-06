# Autosalon Microservices

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-events-orange)
![gRPC](https://img.shields.io/badge/gRPC-sync%20API-blueviolet)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)

> English version: [README.md](README.md)

Autosalon Microservices — учебный backend-проект для системы автосалона. Проект показывает переход к микросервисной архитектуре: отдельные сервисы, отдельные базы данных, асинхронные события через RabbitMQ, синхронные запросы через gRPC, авторизация через Keycloak, миграции Liquibase и тесты.

## Обзор

В проекте два основных микросервиса:

| Сервис | Ответственность |
|---|---|
| `order-service` | Пользователи, конфигурации автомобилей, тест-драйвы, складские заказы, индивидуальные заказы, оплата, outbox-события, gRPC-клиент |
| `storage-service` | Автомобили, запчасти, сборочные заказы, обработка RabbitMQ-событий, gRPC API для доступных автомобилей |

Общий gRPC-контракт лежит здесь:

```text
proto/
```

## Архитектура

```text
Клиент / Postman / curl
        |
        v
order-service :8082
        |
        | gRPC-запрос доступных машин
        v
storage-service :9091
        |
        v
storage PostgreSQL :5433

order-service
        |
        | RabbitMQ-событие: заказ оплачен
        v
storage-service
        |
        v
обработка сборочного заказа
```

## DDD-Структура

В сервисах используется слоистая структура:

```text
domain/          бизнес-модели, enum'ы, события, интерфейсы репозиториев
application/     сценарии использования и application-сервисы
infrastructure/  REST-контроллеры, JPA-адаптеры, gRPC, RabbitMQ, security, exceptions
```

Идея в том, что бизнес-логика не должна зависеть напрямую от базы данных, REST, RabbitMQ или gRPC. Эти детали вынесены в infrastructure-слой.

## Стек

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

## Взаимодействие Сервисов

### RabbitMQ

RabbitMQ используется для асинхронной обработки оплаченных заказов.

```text
order-service: заказ оплачен
order-service: создаёт outbox event
order-service: публикует событие в RabbitMQ
storage-service: получает событие
storage-service: создаёт или обновляет assembly order
```

### gRPC

gRPC используется для синхронного общения между сервисами, когда ответ нужен сразу.

```text
GET /api/v1/cars
-> REST-контроллер order-service
-> gRPC-клиент order-service
-> gRPC-сервер storage-service
-> база storage-service
-> ответ со списком доступных машин
```

## Порты

| Компонент | Порт |
|---|---:|
| order-service | 8082 |
| storage-service HTTP | 8083 |
| storage-service gRPC | 9091 |
| Keycloak | 8081 |
| RabbitMQ | 5672 |
| RabbitMQ Management UI | 15672 |
| order PostgreSQL | 5432 |
| storage PostgreSQL | 5433 |

## Запуск

Поднять инфраструктуру:

```bash
docker compose up -d order-postgres storage-postgres rabbitmq keycloak
```

Выставить Java 21:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
java -version
```

Сначала запустить `storage-service`:

```bash
./gradlew :storage-service:bootRun
```

Во втором терминале запустить `order-service`:

```bash
./gradlew :order-service:bootRun
```

## Пример API

Получить токен пользователя:

```bash
export USER_TOKEN=$(curl -s -X POST "http://localhost:8081/realms/autosalon/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=autosalon-api" \
  -d "username=user1" \
  -d "password=123456" \
  | sed -n 's/.*"access_token":"\([^"]*\)".*/\1/p')
```

Получить доступные автомобили:

```bash
curl -i "http://localhost:8082/api/v1/cars" \
  -H "Authorization: Bearer $USER_TOKEN"
```

Получить автомобиль по id:

```bash
curl -i "http://localhost:8082/api/v1/cars/33333333-3333-3333-3333-333333333333" \
  -H "Authorization: Bearer $USER_TOKEN"
```

## RabbitMQ UI

```text
http://localhost:15672
```

Данные для локального запуска:

```text
username: guest
password: guest
```

## Keycloak

```text
http://localhost:8081
```

Админские данные для локального запуска:

```text
username: admin
password: admin
```

## Тесты

Запустить тесты:

```bash
./gradlew :order-service:test :storage-service:test
```

Проверить компиляцию сервисов:

```bash
./gradlew :order-service:classes :storage-service:classes
```

## Что Реализовано

- Каталог автомобилей и фильтрация
- Управление запчастями
- Валидация конфигурации автомобиля
- Индивидуальные заказы
- Складские заказы
- Заявки на тест-драйв
- JWT-авторизация через Keycloak
- Проверка ролей пользователей
- Отдельная PostgreSQL-база для каждого сервиса
- Liquibase-миграции и seed-данные
- Публикация и обработка событий через RabbitMQ
- Outbox pattern в `order-service`
- Обработка assembly orders в `storage-service`
- Идемпотентная обработка событий
- gRPC API в `storage-service`
- REST endpoints в `order-service`, которые получают данные через gRPC
- HTTP 503, если gRPC-сервис хранения недоступен

## Примечание

Это учебный проект. Пароли и логины в `docker-compose.yml` и `application.yml` используются только для локальной разработки и не подходят для production.
