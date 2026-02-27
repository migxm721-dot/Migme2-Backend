#!/bin/bash
set -e
cd /opt/migme-backend
git pull origin main
cd docker
docker-compose -f docker-compose.yml -f docker-compose.prod.yml pull
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build
echo "Deployment complete"
