# PowerShell script to start PocketBase server for BookLedger
# This script downloads PocketBase if not present and starts the server

Write-Host "🚀 Starting PocketBase server for BookLedger..." -ForegroundColor Green

# Check if PocketBase binary exists
if (-not (Test-Path "pocketbase\pocketbase.exe")) {
    Write-Host "❌ PocketBase binary not found. Downloading..." -ForegroundColor Yellow
    
    # Create pocketbase directory if it doesn't exist
    if (-not (Test-Path "pocketbase")) {
        New-Item -ItemType Directory -Path "pocketbase"
    }
    
    # Download PocketBase (Windows version)
    $url = "https://github.com/pocketbase/pocketbase/releases/latest/download/pocketbase_0.21.0_windows_amd64.zip"
    $zipFile = "pocketbase\pocketbase.zip"
    
    try {
        Invoke-WebRequest -Uri $url -OutFile $zipFile
        Expand-Archive -Path $zipFile -DestinationPath "pocketbase" -Force
        Remove-Item $zipFile
        Write-Host "✅ PocketBase downloaded successfully!" -ForegroundColor Green
    }
    catch {
        Write-Host "❌ Failed to download PocketBase. Please download manually from https://pocketbase.io/docs/" -ForegroundColor Red
        Write-Host "   Place the pocketbase.exe file in the pocketbase directory." -ForegroundColor Red
        exit 1
    }
}

# Change to pocketbase directory
Set-Location "pocketbase"

# Start PocketBase server
Write-Host "📊 Starting server on http://127.0.0.1:8090" -ForegroundColor Cyan
Write-Host "🔧 Admin panel: http://127.0.0.1:8090/_/" -ForegroundColor Cyan
Write-Host "📱 API endpoint: http://127.0.0.1:8090/api/" -ForegroundColor Cyan
Write-Host ""
Write-Host "Press Ctrl+C to stop the server" -ForegroundColor Yellow
Write-Host ""

try {
    .\pocketbase.exe serve --config=./pocketbase.yml
}
catch {
    Write-Host "❌ Failed to start PocketBase server" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Return to parent directory
Set-Location ".."
