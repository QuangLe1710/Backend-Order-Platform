# Backend Order Platform

## Mục tiêu
Project học backend hiện đại theo hướng:
- Spring Boot
- DDD
- Modular Monolith -> Microservices
- Event-driven
- Redis / PostgreSQL / Docker Compose

## Stack
- Java 21
- Spring Boot 3
- Maven
- PostgreSQL
- Redis

## Chạy local
### Build
./mvnw clean test
# Or in Windows
mvnw.cmd clean test

### Run
./mvnw spring-boot:run
# Or in Windows
mvnw.cmd spring-boot:run
``

# Docker build
## step 1:
- docker compose build
## step 2:
- docker compose up