# 🚀 Quick Deploy BookLedger to Render

## Prerequisites
- GitHub account
- Render account (free at [render.com](https://render.com))

## Step 1: Push to GitHub
```bash
git add .
git commit -m "Add PocketBase backend deployment"
git push origin main
```

## Step 2: Deploy to Render

1. **Go to [Render Dashboard](https://dashboard.render.com)**
2. **Click "New +" → "Web Service"**
3. **Connect GitHub repository:**
   - Select `spikebe49/bookledger`
   - Choose `master` branch (not main)
4. **Configure service:**
   - **Name:** `bookledger-backend`
   - **Environment:** `Production` (select this - Docker will be auto-detected)
   - **Build Command:** (leave empty)
   - **Start Command:** (leave empty)
   - **Plan:** `Starter` (free)
   - **Dockerfile Path:** `./Dockerfile`

### 🔧 Alternative: If Docker isn't auto-detected

If you don't see Docker options:
1. **Environment:** Select `Production`
2. **Runtime:** Select `Docker` from dropdown
3. **Dockerfile Path:** `./Dockerfile`

## Step 3: Set Environment Variables

In Render dashboard, add these environment variables:

| Variable | Value |
|----------|-------|
| `POCKETBASE_ADMIN_EMAIL` | `admin@bookledger.com` |
| `POCKETBASE_ADMIN_PASSWORD` | `[Generate strong password]` |
| `POCKETBASE_SECRET_KEY` | `[Generate 32-char random string]` |

## Step 4: Deploy

1. **Click "Create Web Service"**
2. **Wait for deployment** (5-10 minutes)
3. **Get your backend URL:** `https://bookledger-backend.onrender.com`

## Step 5: Test Your Backend

1. **Health Check:**
   ```bash
   curl https://bookledger-backend.onrender.com/api/health
   ```

2. **Admin Panel:**
   - Go to `https://bookledger-backend.onrender.com/_/`
   - Login with your admin credentials

## Step 6: Update Android App

Your Android app is already configured to use the production backend! The app will automatically connect to:
- **Production URL:** `https://bookledger-backend.onrender.com`
- **Development URL:** `http://127.0.0.1:8090` (for local testing)

## 🔧 Configuration

To switch between development and production:

1. **Edit `BackendConfig.kt`:**
   ```kotlin
   // For production
   const val BASE_URL = PRODUCTION_BASE_URL
   
   // For development  
   const val BASE_URL = DEVELOPMENT_BASE_URL
   ```

2. **Rebuild your app**

## 📱 Your App is Ready!

- ✅ **Backend deployed** to Render
- ✅ **Android app configured** for production
- ✅ **Database schema** ready
- ✅ **Authentication** working
- ✅ **Real-time sync** enabled

## 🆘 Need Help?

### **Render Interface Issues:**

**If you can't find Docker option:**
1. Make sure you have a `Dockerfile` in your repository root
2. Try selecting `Production` environment first
3. Look for `Runtime` or `Buildpack` dropdown - select `Docker`
4. If still not working, try creating a new service and select `Docker` as the service type

**If you see "Empty" or no Docker option:**
1. Ensure your repository is properly connected
2. Check that the `Dockerfile` exists in the root directory
3. Try refreshing the page and starting over
4. Make sure you're on the latest Render interface

### **Build Issues:**

**If you see Docker authorization errors:**
- The Dockerfile has been updated to use Alpine Linux and download PocketBase directly
- This avoids GitHub Container Registry authorization issues
- If you still see errors, try redeploying the service

**If you see "403 Forbidden" errors:**
- This was a known issue with the previous Dockerfile
- The new Dockerfile downloads PocketBase binary directly from GitHub releases
- Redeploy your service to use the updated Dockerfile

### **Other Issues:**
- **Deployment Issues:** Check Render logs in the dashboard
- **App Issues:** Check Android Studio Logcat
- **API Issues:** Test endpoints in admin panel
- **Build Issues:** Check the build logs in Render dashboard

---

**🎉 Congratulations! Your BookLedger app now has a production backend!**
