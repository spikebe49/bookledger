# BookLedger Backend Deployment Guide

This guide will help you deploy the BookLedger PocketBase backend to Render.

## Prerequisites

1. A GitHub account
2. A Render account (sign up at [render.com](https://render.com))
3. Your BookLedger repository pushed to GitHub

## Step 1: Prepare Your Repository

1. **Push your code to GitHub:**
   ```bash
   git add .
   git commit -m "Add PocketBase backend deployment files"
   git push origin main
   ```

2. **Make sure these files are in your repository:**
   - `Dockerfile`
   - `render.yaml`
   - `start.sh`
   - `pocketbase/pocketbase-prod.yml`
   - `pocketbase/pb_schema.json`
   - `pocketbase/pb_migrations.json`
   - `pocketbase/pb_hooks.js`

## Step 2: Deploy to Render

1. **Go to [Render Dashboard](https://dashboard.render.com)**

2. **Click "New +" → "Web Service"**

3. **Connect your GitHub repository:**
   - Select your `bookledger` repository
   - Choose the `main` branch

4. **Configure the service:**
   - **Name:** `bookledger-backend`
   - **Environment:** `Docker`
   - **Dockerfile Path:** `./Dockerfile`
   - **Plan:** `Starter` (free tier)

5. **Set Environment Variables:**
   - `POCKETBASE_ADMIN_EMAIL`: `admin@bookledger.com`
   - `POCKETBASE_ADMIN_PASSWORD`: `[Generate a strong password]`
   - `POCKETBASE_SECRET_KEY`: `[Generate a random 32-character string]`

6. **Configure Advanced Settings:**
   - **Health Check Path:** `/api/health`
   - **Auto-Deploy:** `Yes`

7. **Click "Create Web Service"**

## Step 3: Configure Your Android App

Once deployed, you'll get a URL like: `https://bookledger-backend.onrender.com`

Update your Android app's PocketBase configuration:

1. **Update the base URL in your app:**
   ```kotlin
   // In your PocketBase configuration
   val baseUrl = "https://bookledger-backend.onrender.com"
   ```

2. **Update CORS settings** (if needed):
   - Go to your Render service dashboard
   - Add your app's domain to CORS origins

## Step 4: Initialize the Database

1. **Access the PocketBase Admin UI:**
   - Go to `https://your-service-url.onrender.com/_/`
   - Login with your admin credentials

2. **Import your schema:**
   - The schema should be automatically loaded from `pb_schema.json`
   - If not, manually create the collections:
     - `categories`
     - `expenses` 
     - `sales`

## Step 5: Test Your Backend

1. **Test the API:**
   ```bash
   curl https://your-service-url.onrender.com/api/health
   ```

2. **Test authentication:**
   ```bash
   curl -X POST https://your-service-url.onrender.com/api/collections/users/auth-with-password \
     -H "Content-Type: application/json" \
     -d '{"identity":"test@example.com","password":"testpassword"}'
   ```

## Environment Variables Reference

| Variable | Description | Example |
|----------|-------------|---------|
| `POCKETBASE_ADMIN_EMAIL` | Admin email for PocketBase | `admin@bookledger.com` |
| `POCKETBASE_ADMIN_PASSWORD` | Admin password | `SecurePassword123!` |
| `POCKETBASE_SECRET_KEY` | Secret key for JWT tokens | `32-character-random-string` |

## Troubleshooting

### Common Issues:

1. **Service won't start:**
   - Check the logs in Render dashboard
   - Ensure all files are properly committed to GitHub

2. **Database not initialized:**
   - Check if the data directory has proper permissions
   - Verify the database file is being created

3. **CORS errors:**
   - Update the CORS origins in `pocketbase-prod.yml`
   - Redeploy the service

4. **Authentication issues:**
   - Verify environment variables are set correctly
   - Check the admin user was created properly

### Logs and Monitoring:

- View logs in the Render dashboard
- Monitor service health and performance
- Set up alerts for downtime

## Security Considerations

1. **Change default passwords** immediately after deployment
2. **Use strong secret keys** for production
3. **Enable HTTPS** (Render provides this automatically)
4. **Regular backups** of your database
5. **Monitor access logs** for suspicious activity

## Scaling

- **Starter Plan:** Free tier, good for development and small apps
- **Standard Plan:** $7/month, better performance and reliability
- **Pro Plan:** $25/month, production-ready with better support

## Support

- [Render Documentation](https://render.com/docs)
- [PocketBase Documentation](https://pocketbase.io/docs/)
- [BookLedger Issues](https://github.com/spikebe49/bookledger/issues)

---

**Note:** The free tier on Render may spin down your service after 15 minutes of inactivity. For production use, consider upgrading to a paid plan.
