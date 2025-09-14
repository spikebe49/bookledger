# 🚀 PocketBase Setup for BookLedger

This guide will help you set up and run the PocketBase server for the BookLedger Android app.

## 📋 Prerequisites

- Windows 10/11 with PowerShell 5.1+ or Windows PowerShell
- Internet connection for downloading PocketBase
- Android Studio with the BookLedger project

## 🚀 Quick Start

### Option 1: Automated Setup (Recommended)

**Windows:**
```powershell
.\start-pocketbase.ps1
```

**Linux/macOS:**
```bash
./start-pocketbase.sh
```

### Option 2: Manual Setup

1. **Download PocketBase:**
   - Go to [https://pocketbase.io/docs/](https://pocketbase.io/docs/)
   - Download the appropriate version for your OS
   - Extract the `pocketbase` binary to the `pocketbase/` directory

2. **Start the Server:**
   ```bash
   cd pocketbase
   ./pocketbase serve --config=./pocketbase.yml
   ```

## 🌐 Server Endpoints

Once running, you can access:

- **Admin Panel**: http://127.0.0.1:8090/_/
- **API Base**: http://127.0.0.1:8090/api/
- **Dashboard Stats**: http://127.0.0.1:8090/api/dashboard-stats
- **Monthly Stats**: http://127.0.0.1:8090/api/monthly-stats

## 🔐 Default Admin Account

- **Email**: admin@bookledger.com
- **Password**: admin123

## 📱 Android App Configuration

The Android app is configured to connect to `http://127.0.0.1:8090` by default. If you need to change this:

1. Open `app/src/main/java/com/juan/bookledger/data/remote/PocketBaseService.kt`
2. Update the URL in the constructor:
   ```kotlin
   private val pocketBase = PocketBase("http://YOUR_SERVER_IP:8090")
   ```

## 🗄️ Database Schema

The server includes three main collections:

### Categories
- Stores expense and income categories
- User-specific with proper access controls
- Pre-populated with default categories

### Expenses
- Business expense records
- Linked to categories and users
- Includes amount, description, date, and notes

### Sales
- Sales transaction records
- Linked to categories and users
- Includes customer information and notes

## 🔄 Data Synchronization

The app includes a hybrid approach:
- **Local Storage**: Room database for offline functionality
- **Cloud Sync**: PocketBase for real-time updates and backup
- **Automatic Sync**: Data syncs when network is available

## 🛠️ Customization

### Server Configuration
Edit `pocketbase/pocketbase.yml` to customize:
- Server ports and addresses
- Database settings
- Admin credentials
- CORS origins
- Security settings

### Database Schema
- `pocketbase/pb_schema.json`: Collection definitions
- `pocketbase/pb_migrations.json`: Database migrations
- `pocketbase/pb_hooks.js`: Custom API endpoints and business logic

## 🚨 Troubleshooting

### Common Issues

1. **Port Already in Use**
   - Change the port in `pocketbase.yml`
   - Or stop other services using port 8090

2. **Permission Denied (Linux/macOS)**
   ```bash
   chmod +x pocketbase/pocketbase
   chmod +x start-pocketbase.sh
   ```

3. **Download Failed**
   - Check internet connection
   - Download PocketBase manually from GitHub releases
   - Place binary in `pocketbase/` directory

4. **Android App Can't Connect**
   - Ensure server is running
   - Check firewall settings
   - Verify IP address in PocketBaseService.kt

### Logs and Debugging

- Server logs appear in the console
- Check the admin panel for database status
- Android logs in Android Studio Logcat

## 🔒 Security Notes

### Development
- Default credentials are for development only
- Change admin password before production
- Use HTTPS in production

### Production Deployment
1. Set strong admin password
2. Configure HTTPS with SSL certificates
3. Set up proper CORS origins
4. Use environment variables for sensitive data
5. Set up automated backups
6. Configure reverse proxy (nginx/Apache)

## 📊 Monitoring

- **Real-time Connections**: Monitor in admin panel
- **Database Size**: Check in admin panel
- **API Usage**: Monitor request logs
- **Backups**: Automatic daily backups (configurable)

## 🔄 Backup and Restore

### Automatic Backups
- Configured in `pocketbase.yml`
- Daily backups at 2 AM
- Keeps last 7 backups

### Manual Backup
```bash
cd pocketbase
./pocketbase admin backup
```

### Restore
```bash
cd pocketbase
./pocketbase admin restore backup_file.zip
```

## 📚 Additional Resources

- [PocketBase Documentation](https://pocketbase.io/docs/)
- [API Reference](https://pocketbase.io/docs/api-records/)
- [Hooks Documentation](https://pocketbase.io/docs/js-overview/)
- [BookLedger Android App README](./README.md)

## 🆘 Support

If you encounter issues:

1. Check the troubleshooting section above
2. Review server logs in the console
3. Check Android Studio Logcat for app errors
4. Verify network connectivity
5. Ensure all dependencies are properly installed

---

**Happy coding! 🎉** Your BookLedger app is now ready with a powerful backend server.
