# PocketBase Server for BookLedger

This directory contains the PocketBase server configuration and setup for the BookLedger Android app.

## 🚀 Quick Start

### Prerequisites
- Download PocketBase binary from [https://pocketbase.io/docs/](https://pocketbase.io/docs/)
- Place the `pocketbase` (or `pocketbase.exe` on Windows) binary in this directory

### Starting the Server

**Linux/macOS:**
```bash
chmod +x start.sh
./start.sh
```

**Windows:**
```cmd
start.bat
```

**Manual start:**
```bash
./pocketbase serve --config=./pocketbase.yml
```

## 📊 Server Endpoints

- **Admin Panel**: http://127.0.0.1:8090/_/
- **API Base**: http://127.0.0.1:8090/api/
- **Dashboard Stats**: http://127.0.0.1:8090/api/dashboard-stats
- **Monthly Stats**: http://127.0.0.1:8090/api/monthly-stats

## 🗄️ Database Schema

### Collections

#### Categories
- `id` (Text, Primary Key)
- `name` (Text, Required)
- `type` (Select: EXPENSE/INCOME, Required)
- `color` (Text, Hex color code)
- `user_id` (Relation to users)

#### Expenses
- `id` (Text, Primary Key)
- `amount` (Number, Required, Min: 0)
- `description` (Text, Required, 1-255 chars)
- `category_id` (Relation to categories)
- `date` (Date, Required)
- `notes` (Text, Optional, Max: 1000 chars)
- `user_id` (Relation to users)

#### Sales
- `id` (Text, Primary Key)
- `amount` (Number, Required, Min: 0)
- `description` (Text, Required, 1-255 chars)
- `category_id` (Relation to categories)
- `date` (Date, Required)
- `customer_name` (Text, Optional, Max: 100 chars)
- `notes` (Text, Optional, Max: 1000 chars)
- `user_id` (Relation to users)

## 🔐 Authentication

The server uses PocketBase's built-in authentication system:
- User registration and login
- JWT token-based authentication
- Password requirements: 8+ chars, uppercase, lowercase, numbers, special chars
- Token duration: 24 hours

## 📡 Real-time Features

- Live updates via WebSocket connections
- Broadcasts changes to all connected clients
- Channel: `bookledger_updates`

## 🛠️ Custom API Endpoints

### Dashboard Statistics
```
GET /api/dashboard-stats
```
Returns current month's financial overview including:
- Total expenses and sales
- Net profit
- Recent transactions
- Category breakdowns

### Monthly Statistics
```
GET /api/monthly-stats?year=2024&month=1
```
Returns statistics for a specific month.

## 🔧 Configuration

Edit `pocketbase.yml` to customize:
- Server ports and addresses
- Database settings
- Admin credentials
- CORS origins
- Security settings
- Email configuration

## 📁 File Structure

```
pocketbase/
├── pocketbase.yml          # Server configuration
├── pb_schema.json          # Database schema definition
├── pb_data.json            # Default data (categories)
├── pb_migrations.json      # Database migrations
├── pb_hooks.js             # Server-side hooks and custom endpoints
├── start.sh                # Linux/macOS start script
├── start.bat               # Windows start script
└── README.md               # This file
```

## 🚀 Production Deployment

For production deployment:

1. **Change default credentials** in `pocketbase.yml`
2. **Set a strong secret key** for JWT tokens
3. **Configure HTTPS** with proper certificates
4. **Set up email** for password resets
5. **Configure backup** settings
6. **Set up reverse proxy** (nginx/Apache)
7. **Use environment variables** for sensitive data

## 🔍 Monitoring

- Check logs in the admin panel
- Monitor real-time connections
- Set up automated backups
- Monitor disk usage in `./data/` directory

## 🐛 Troubleshooting

### Common Issues

1. **Port already in use**: Change the port in `pocketbase.yml`
2. **Permission denied**: Make sure the binary has execute permissions
3. **Database locked**: Stop other PocketBase instances
4. **CORS errors**: Add your app's domain to CORS origins

### Logs
Check the console output for detailed error messages and server logs.

## 📚 Documentation

- [PocketBase Documentation](https://pocketbase.io/docs/)
- [API Reference](https://pocketbase.io/docs/api-records/)
- [Hooks Documentation](https://pocketbase.io/docs/js-overview/)
