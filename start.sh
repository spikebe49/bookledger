#!/bin/bash

# Start script for PocketBase on Render
# This script initializes the database and starts PocketBase

set -e

echo "Starting BookLedger PocketBase Backend..."

# Create data directory if it doesn't exist
mkdir -p /app/data

# Set permissions
chmod 755 /app/data

# Initialize database if it doesn't exist
if [ ! -f "/app/data/bookledger.db" ]; then
    echo "Initializing database..."
    /app/pocketbase migrate
    echo "Database initialized successfully"
fi

# Set admin credentials from environment variables
if [ ! -z "$POCKETBASE_ADMIN_EMAIL" ] && [ ! -z "$POCKETBASE_ADMIN_PASSWORD" ]; then
    echo "Setting up admin user..."
    /app/pocketbase admin create $POCKETBASE_ADMIN_EMAIL $POCKETBASE_ADMIN_PASSWORD
fi

# Start PocketBase
echo "Starting PocketBase server..."
exec /app/pocketbase serve --http=0.0.0.0:8090
