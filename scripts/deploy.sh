#!/bin/bash
# Deploy script for Migme2-Backend
set -e

PROFILE=${SPRING_PROFILES_ACTIVE:-prod}
echo "Deploying Migme2-Backend with profile: $PROFILE"

# Build the application
echo "Building application..."
mvn clean package -DskipTests

# Run with Docker Compose
echo "Starting services with Docker Compose..."
cd docker
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build

echo "Deployment complete. Services running on port 8080."
