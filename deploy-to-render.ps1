# Deploy BookLedger to Render
# This PowerShell script helps you deploy your PocketBase backend to Render

Write-Host "🚀 BookLedger Backend Deployment Script" -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Green

# Check if git is available
if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Git is not installed. Please install Git first." -ForegroundColor Red
    exit 1
}

# Check if we're in a git repository
if (-not (Test-Path ".git")) {
    Write-Host "❌ Not in a git repository. Please run this from your project root." -ForegroundColor Red
    exit 1
}

# Check if files exist
$requiredFiles = @("Dockerfile", "render.yaml", "start.sh", "pocketbase/pocketbase-prod.yml")
foreach ($file in $requiredFiles) {
    if (-not (Test-Path $file)) {
        Write-Host "❌ Required file $file not found. Please ensure all deployment files are present." -ForegroundColor Red
        exit 1
    }
}

Write-Host "✅ All required files found" -ForegroundColor Green

# Generate random secret key if not set
if (-not $env:POCKETBASE_SECRET_KEY) {
    Write-Host "🔑 Generating random secret key..." -ForegroundColor Yellow
    $env:POCKETBASE_SECRET_KEY = -join ((1..32) | ForEach {[char]((65..90) + (97..122) + (48..57) | Get-Random)})
    Write-Host "Generated secret key: $($env:POCKETBASE_SECRET_KEY.Substring(0,8))..." -ForegroundColor Yellow
    Write-Host "⚠️  IMPORTANT: Save this secret key securely!" -ForegroundColor Yellow
}

# Generate random admin password if not set
if (-not $env:POCKETBASE_ADMIN_PASSWORD) {
    Write-Host "🔑 Generating random admin password..." -ForegroundColor Yellow
    $env:POCKETBASE_ADMIN_PASSWORD = -join ((1..32) | ForEach {[char]((65..90) + (97..122) + (48..57) | Get-Random)})
    Write-Host "Generated admin password: $($env:POCKETBASE_ADMIN_PASSWORD.Substring(0,8))..." -ForegroundColor Yellow
    Write-Host "⚠️  IMPORTANT: Save this password securely!" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "📋 Deployment Checklist:" -ForegroundColor Cyan
Write-Host "========================" -ForegroundColor Cyan
Write-Host "1. ✅ All deployment files present" -ForegroundColor Green
Write-Host "2. ✅ Secret key generated: $($env:POCKETBASE_SECRET_KEY.Substring(0,8))..." -ForegroundColor Green
Write-Host "3. ✅ Admin password generated: $($env:POCKETBASE_ADMIN_PASSWORD.Substring(0,8))..." -ForegroundColor Green
Write-Host ""
Write-Host "📝 Next Steps:" -ForegroundColor Cyan
Write-Host "==============" -ForegroundColor Cyan
Write-Host "1. Push your code to GitHub:" -ForegroundColor White
Write-Host "   git add ." -ForegroundColor Gray
Write-Host "   git commit -m 'Add PocketBase backend deployment'" -ForegroundColor Gray
Write-Host "   git push origin main" -ForegroundColor Gray
Write-Host ""
Write-Host "2. Go to https://dashboard.render.com" -ForegroundColor White
Write-Host "3. Create a new Web Service" -ForegroundColor White
Write-Host "4. Connect your GitHub repository" -ForegroundColor White
Write-Host "5. Use these environment variables:" -ForegroundColor White
Write-Host "   POCKETBASE_ADMIN_EMAIL=admin@bookledger.com" -ForegroundColor Gray
Write-Host "   POCKETBASE_ADMIN_PASSWORD=$env:POCKETBASE_ADMIN_PASSWORD" -ForegroundColor Gray
Write-Host "   POCKETBASE_SECRET_KEY=$env:POCKETBASE_SECRET_KEY" -ForegroundColor Gray
Write-Host ""
Write-Host "6. Deploy and test your backend!" -ForegroundColor White
Write-Host ""
Write-Host "🔗 Your backend will be available at: https://bookledger-backend.onrender.com" -ForegroundColor Green
Write-Host ""
Write-Host "📚 For detailed instructions, see README-DEPLOYMENT.md" -ForegroundColor Cyan
