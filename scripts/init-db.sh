#!/bin/bash
# Initialize the database with schema migrations
set -e

DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-5432}
DB_NAME=${DB_NAME:-migmedb}
DB_USERNAME=${DB_USERNAME:-migmeuser}
DB_PASSWORD=${DB_PASSWORD:-changeme}

echo "Initializing database $DB_NAME on $DB_HOST:$DB_PORT..."

# Wait for database to be ready
until PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -c '\q' 2>/dev/null; do
    echo "Waiting for database to be ready..."
    sleep 2
done

echo "Database is ready. Running Flyway migrations..."

PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME" -c "CREATE DATABASE $DB_NAME;" 2>/dev/null || echo "Database $DB_NAME already exists."

echo "Database initialization complete. Flyway will run migrations on application startup."
