#!/bin/bash
# Run the application locally for development
set -e

export SPRING_PROFILES_ACTIVE=dev
export DB_HOST=${DB_HOST:-localhost}
export DB_PORT=${DB_PORT:-5432}
export DB_NAME=${DB_NAME:-migmedb}
export DB_USERNAME=${DB_USERNAME:-migmeuser}
export DB_PASSWORD=${DB_PASSWORD:-changeme}
export REDIS_HOST=${REDIS_HOST:-localhost}
export REDIS_PORT=${REDIS_PORT:-6379}
export JWT_SECRET=${JWT_SECRET:-dev-secret-key-change-in-production}

echo "Starting Migme2-Backend in development mode..."
echo "REST API: http://localhost:8080/api"
echo "TCP Socket: localhost:9119"

mvn spring-boot:run -Dspring-boot.run.profiles=dev
