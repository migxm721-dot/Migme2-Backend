# Migme Backend Server

Complete Java Spring Boot backend server for Migme Chat Application.

## Features
- TCP Socket Server (Port 9119) - Fusion Protocol
- REST API (Port 8080)
- PostgreSQL Database
- Redis Cache
- JWT Authentication
- Private Chat, Group Chat, Public Chat Rooms
- Docker Support
- AWS Deployment Ready

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose

### Local Development

```bash
cd docker
docker-compose up -d postgres redis
./mvnw spring-boot:run
```

Access:
- REST API: http://localhost:8080/api
- Health: http://localhost:8080/api/health
- TCP: localhost:9119

### Docker

```bash
cd docker && docker-compose up -d
```

## Configuration
Set environment variables or edit `application.yml`.
