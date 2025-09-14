# Test script to verify PocketBase connection
Write-Host "🧪 Testing PocketBase connection..." -ForegroundColor Green

# Test if PocketBase is running
try {
    $response = Invoke-WebRequest -Uri "http://127.0.0.1:8090/api/health" -Method GET -TimeoutSec 5
    Write-Host "✅ PocketBase server is running" -ForegroundColor Green
    Write-Host "📊 Status: $($response.StatusCode)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ PocketBase server is not running" -ForegroundColor Red
    Write-Host "   Please start it with: cd pocketbase && .\pocketbase.exe serve" -ForegroundColor Yellow
    exit 1
}

# Test admin panel access
try {
    $response = Invoke-WebRequest -Uri "http://127.0.0.1:8090/_/" -Method GET -TimeoutSec 5
    Write-Host "✅ Admin panel is accessible" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Admin panel may not be accessible" -ForegroundColor Yellow
}

# Test API endpoint
try {
    $response = Invoke-WebRequest -Uri "http://127.0.0.1:8090/api/collections" -Method GET -TimeoutSec 5
    Write-Host "✅ API endpoint is working" -ForegroundColor Green
} catch {
    Write-Host "⚠️ API endpoint may not be ready yet" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "🌐 Access URLs:" -ForegroundColor Cyan
Write-Host "   Admin Panel: http://127.0.0.1:8090/_/" -ForegroundColor White
Write-Host "   API Base: http://127.0.0.1:8090/api/" -ForegroundColor White
Write-Host ""
Write-Host "📱 Your Android app should now be able to connect!" -ForegroundColor Green
