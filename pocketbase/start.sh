#!/bin/bash

# PocketBase start script for BookLedger app
# This script starts the PocketBase server with the BookLedger configuration

echo "🚀 Starting PocketBase server for BookLedger..."

# Check if PocketBase binary exists
if [ ! -f "./pocketbase" ]; then
    echo "❌ PocketBase binary not found. Please download it from https://pocketbase.io/docs/"
    echo "   Download the appropriate version for your platform and place it in this directory."
    exit 1
fi

# Create data directory if it doesn't exist
mkdir -p ./data

# Create backups directory if it doesn't exist
mkdir -p ./backups

# Set executable permissions
chmod +x ./pocketbase

# Start PocketBase server
echo "📊 Starting server on http://127.0.0.1:8090"
echo "🔧 Admin panel: http://127.0.0.1:8090/_/"
echo "📱 API endpoint: http://127.0.0.1:8090/api/"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

./pocketbase serve --config=./pocketbase.yml
