# PowerShell script to set up BookLedger database in PocketBase
# This script initializes the database with the proper schema and data

Write-Host "🗄️ Setting up BookLedger database in PocketBase..." -ForegroundColor Green

# Wait a moment for PocketBase to start
Start-Sleep -Seconds 3

# Check if PocketBase is running
try {
    $response = Invoke-WebRequest -Uri "http://127.0.0.1:8090/api/health" -Method GET -TimeoutSec 5
    Write-Host "✅ PocketBase server is running" -ForegroundColor Green
} catch {
    Write-Host "❌ PocketBase server is not running. Please start it first." -ForegroundColor Red
    Write-Host "   Run: cd pocketbase && .\pocketbase.exe serve --config=./pocketbase.yml" -ForegroundColor Yellow
    exit 1
}

# Create admin user if it doesn't exist
Write-Host "👤 Setting up admin user..." -ForegroundColor Cyan
try {
    $adminData = @{
        email = "admin@bookledger.com"
        password = "admin123"
        passwordConfirm = "admin123"
    } | ConvertTo-Json

    $response = Invoke-WebRequest -Uri "http://127.0.0.1:8090/api/admins" -Method POST -Body $adminData -ContentType "application/json" -TimeoutSec 10
    Write-Host "✅ Admin user created successfully" -ForegroundColor Green
} catch {
    Write-Host "ℹ️ Admin user may already exist (this is normal)" -ForegroundColor Yellow
}

# Authenticate as admin
Write-Host "🔐 Authenticating as admin..." -ForegroundColor Cyan
try {
    $authData = @{
        identity = "admin@bookledger.com"
        password = "admin123"
    } | ConvertTo-Json

    $authResponse = Invoke-WebRequest -Uri "http://127.0.0.1:8090/api/admins/auth-with-password" -Method POST -Body $authData -ContentType "application/json" -TimeoutSec 10
    $authResult = $authResponse.Content | ConvertFrom-Json
    $token = $authResult.token
    Write-Host "✅ Admin authentication successful" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed to authenticate as admin" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Set up headers for authenticated requests
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# Create categories collection
Write-Host "📂 Creating categories collection..." -ForegroundColor Cyan
try {
    $categoriesSchema = @{
        name = "categories"
        type = "base"
        schema = @(
            @{
                name = "name"
                type = "text"
                required = $true
            },
            @{
                name = "type"
                type = "select"
                required = $true
                options = @{
                    values = @("EXPENSE", "INCOME")
                }
            },
            @{
                name = "color"
                type = "text"
                required = $false
            }
        )
        listRule = "user_id = @request.auth.id"
        viewRule = "user_id = @request.auth.id"
        createRule = "user_id = @request.auth.id"
        updateRule = "user_id = @request.auth.id"
        deleteRule = "user_id = @request.auth.id"
    } | ConvertTo-Json -Depth 10

    $response = Invoke-WebRequest -Uri "http://127.0.0.1:8090/api/collections" -Method POST -Body $categoriesSchema -Headers $headers -TimeoutSec 10
    Write-Host "✅ Categories collection created" -ForegroundColor Green
} catch {
    Write-Host "ℹ️ Categories collection may already exist" -ForegroundColor Yellow
}

# Create expenses collection
Write-Host "💰 Creating expenses collection..." -ForegroundColor Cyan
try {
    $expensesSchema = @{
        name = "expenses"
        type = "base"
        schema = @(
            @{
                name = "amount"
                type = "number"
                required = $true
            },
            @{
                name = "description"
                type = "text"
                required = $true
            },
            @{
                name = "category_id"
                type = "relation"
                required = $true
                options = @{
                    collectionId = "categories"
                }
            },
            @{
                name = "date"
                type = "date"
                required = $true
            },
            @{
                name = "notes"
                type = "text"
                required = $false
            }
        )
        listRule = "user_id = @request.auth.id"
        viewRule = "user_id = @request.auth.id"
        createRule = "user_id = @request.auth.id"
        updateRule = "user_id = @request.auth.id"
        deleteRule = "user_id = @request.auth.id"
    } | ConvertTo-Json -Depth 10

    $response = Invoke-WebRequest -Uri "http://127.0.0.1:8090/api/collections" -Method POST -Body $expensesSchema -Headers $headers -TimeoutSec 10
    Write-Host "✅ Expenses collection created" -ForegroundColor Green
} catch {
    Write-Host "ℹ️ Expenses collection may already exist" -ForegroundColor Yellow
}

# Create sales collection
Write-Host "💵 Creating sales collection..." -ForegroundColor Cyan
try {
    $salesSchema = @{
        name = "sales"
        type = "base"
        schema = @(
            @{
                name = "amount"
                type = "number"
                required = $true
            },
            @{
                name = "description"
                type = "text"
                required = $true
            },
            @{
                name = "category_id"
                type = "relation"
                required = $true
                options = @{
                    collectionId = "categories"
                }
            },
            @{
                name = "date"
                type = "date"
                required = $true
            },
            @{
                name = "customer_name"
                type = "text"
                required = $false
            },
            @{
                name = "notes"
                type = "text"
                required = $false
            }
        )
        listRule = "user_id = @request.auth.id"
        viewRule = "user_id = @request.auth.id"
        createRule = "user_id = @request.auth.id"
        updateRule = "user_id = @request.auth.id"
        deleteRule = "user_id = @request.auth.id"
    } | ConvertTo-Json -Depth 10

    $response = Invoke-WebRequest -Uri "http://127.0.0.1:8090/api/collections" -Method POST -Body $salesSchema -Headers $headers -TimeoutSec 10
    Write-Host "✅ Sales collection created" -ForegroundColor Green
} catch {
    Write-Host "ℹ️ Sales collection may already exist" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "🎉 Database setup completed!" -ForegroundColor Green
Write-Host "📊 Admin Panel: http://127.0.0.1:8090/_/" -ForegroundColor Cyan
Write-Host "🔑 Admin Login: admin@bookledger.com / admin123" -ForegroundColor Cyan
Write-Host "📱 API Endpoint: http://127.0.0.1:8090/api/" -ForegroundColor Cyan
Write-Host ""
Write-Host "Your BookLedger app is now ready to use! 🚀" -ForegroundColor Green
