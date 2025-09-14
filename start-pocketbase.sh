#!/bin/bash

# Bash script to start PocketBase server for BookLedger
# This script downloads PocketBase if not present and starts the server

echo "🚀 Starting PocketBase server for BookLedger..."

# Check if PocketBase binary exists
if [ ! -f "pocketbase/pocketbase" ]; then
    echo "❌ PocketBase binary not found. Downloading..."
    
    # Create pocketbase directory if it doesn't exist
    mkdir -p pocketbase
    
    # Detect OS and architecture
    OS=$(uname -s | tr '[:upper:]' '[:lower:]')
    ARCH=$(uname -m)
    
    case $ARCH in
        x86_64) ARCH="amd64" ;;
        arm64) ARCH="arm64" ;;
        armv7l) ARCH="armv7" ;;
        *) echo "❌ Unsupported architecture: $ARCH"; exit 1 ;;
    esac
    
    # Download PocketBase
    URL="https://github.com/pocketbase/pocketbase/releases/latest/download/pocketbase_0.21.0_${OS}_${ARCH}.zip"
    ZIP_FILE="pocketbase/pocketbase.zip"
    
    echo "📥 Downloading from: $URL"
    
    if command -v curl &> /dev/null; then
        curl -L -o "$ZIP_FILE" "$URL"
    elif command -v wget &> /dev/null; then
        wget -O "$ZIP_FILE" "$URL"
    else
        echo "❌ Neither curl nor wget found. Please install one of them or download PocketBase manually."
        exit 1
    fi
    
    if [ $? -eq 0 ]; then
        cd pocketbase
        unzip -o pocketbase.zip
        rm pocketbase.zip
        chmod +x pocketbase
        cd ..
        echo "✅ PocketBase downloaded successfully!"
    else
        echo "❌ Failed to download PocketBase. Please download manually from https://pocketbase.io/docs/"
        echo "   Place the pocketbase binary in the pocketbase directory."
        exit 1
    fi
fi

# Change to pocketbase directory
cd pocketbase

# Start PocketBase server
echo "📊 Starting server on http://127.0.0.1:8090"
echo "🔧 Admin panel: http://127.0.0.1:8090/_/"
echo "📱 API endpoint: http://127.0.0.1:8090/api/"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

./pocketbase serve --config=./pocketbase.yml
