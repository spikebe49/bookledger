@echo off
REM PocketBase start script for BookLedger app (Windows)
REM This script starts the PocketBase server with the BookLedger configuration

echo 🚀 Starting PocketBase server for BookLedger...

REM Check if PocketBase binary exists
if not exist "pocketbase.exe" (
    echo ❌ PocketBase binary not found. Please download it from https://pocketbase.io/docs/
    echo    Download the appropriate version for your platform and place it in this directory.
    pause
    exit /b 1
)

REM Create data directory if it doesn't exist
if not exist "data" mkdir data

REM Create backups directory if it doesn't exist
if not exist "backups" mkdir backups

REM Start PocketBase server
echo 📊 Starting server on http://127.0.0.1:8090
echo 🔧 Admin panel: http://127.0.0.1:8090/_/
echo 📱 API endpoint: http://127.0.0.1:8090/api/
echo.
echo Press Ctrl+C to stop the server
echo.

pocketbase.exe serve --config=./pocketbase.yml
