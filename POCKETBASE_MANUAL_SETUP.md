# 🛠️ Manual PocketBase Setup for BookLedger

Since the automated setup had issues, here's how to manually configure PocketBase for your BookLedger app:

## 🚀 Step 1: Access the Admin Panel

1. Open your browser and go to: **http://127.0.0.1:8090/_/**
2. You should see the PocketBase admin login screen

## 👤 Step 2: Create Admin Account

1. Click "Create Admin" or "Sign Up"
2. Enter the following details:
   - **Email**: `admin@bookledger.com`
   - **Password**: `admin123`
   - **Confirm Password**: `admin123`
3. Click "Create Admin"

## 📂 Step 3: Create Collections

### 3.1 Create Categories Collection

1. Click "New Collection" in the admin panel
2. Set the following:
   - **Name**: `categories`
   - **Type**: `Base`
3. Click "Create"
4. Go to the "Schema" tab and add these fields:

| Field Name | Type | Required | Options |
|------------|------|----------|---------|
| `name` | Text | ✅ | - |
| `type` | Select | ✅ | Values: `EXPENSE`, `INCOME` |
| `color` | Text | ❌ | - |

5. Go to the "Rules" tab and set:
   - **List rule**: `user_id = @request.auth.id`
   - **View rule**: `user_id = @request.auth.id`
   - **Create rule**: `user_id = @request.auth.id`
   - **Update rule**: `user_id = @request.auth.id`
   - **Delete rule**: `user_id = @request.auth.id`

### 3.2 Create Expenses Collection

1. Click "New Collection"
2. Set:
   - **Name**: `expenses`
   - **Type**: `Base`
3. Click "Create"
4. Add these fields in the Schema tab:

| Field Name | Type | Required | Options |
|------------|------|----------|---------|
| `amount` | Number | ✅ | Min: 0 |
| `description` | Text | ✅ | Max: 255 |
| `category_id` | Relation | ✅ | Collection: `categories` |
| `date` | Date | ✅ | - |
| `notes` | Text | ❌ | Max: 1000 |

5. Set the same rules as categories in the Rules tab

### 3.3 Create Sales Collection

1. Click "New Collection"
2. Set:
   - **Name**: `sales`
   - **Type**: `Base`
3. Click "Create"
4. Add these fields in the Schema tab:

| Field Name | Type | Required | Options |
|------------|------|----------|---------|
| `amount` | Number | ✅ | Min: 0 |
| `description` | Text | ✅ | Max: 255 |
| `category_id` | Relation | ✅ | Collection: `categories` |
| `date` | Date | ✅ | - |
| `customer_name` | Text | ❌ | Max: 100 |
| `notes` | Text | ❌ | Max: 1000 |

5. Set the same rules as categories in the Rules tab

## 📊 Step 4: Add Default Categories

1. Go to the "Data" tab for the `categories` collection
2. Click "New Record" and add these expense categories:

| Name | Type | Color |
|------|------|-------|
| Office Supplies | EXPENSE | #FF5722 |
| Utilities | EXPENSE | #2196F3 |
| Rent | EXPENSE | #9C27B0 |
| Marketing | EXPENSE | #FF9800 |
| Travel | EXPENSE | #4CAF50 |
| Equipment | EXPENSE | #795548 |

3. Add these income categories:

| Name | Type | Color |
|------|------|-------|
| Book Sales | INCOME | #4CAF50 |
| Consulting | INCOME | #2196F3 |
| Workshops | INCOME | #FF9800 |
| Royalties | INCOME | #9C27B0 |
| Speaking | INCOME | #FF5722 |
| Other Income | INCOME | #607D8B |

## 🔧 Step 5: Configure Settings

1. Go to "Settings" in the admin panel
2. Under "General":
   - Set **Site URL**: `http://127.0.0.1:8090`
   - Set **Admin Email**: `admin@bookledger.com`
3. Under "Auth":
   - Enable **Email/Password** authentication
   - Set **Password min length**: 8
4. Under "API":
   - Enable **CORS** for `http://localhost:3000` and `http://127.0.0.1:3000`

## ✅ Step 6: Test the Setup

1. Go to the "API" tab in the admin panel
2. You should see endpoints for:
   - `/api/collections/categories`
   - `/api/collections/expenses`
   - `/api/collections/sales`
3. Test creating a record using the API explorer

## 📱 Step 7: Update Android App

Your Android app is already configured to connect to PocketBase. The app will:

1. **Start with login screen** - users can register/login
2. **Sync data automatically** - expenses and sales sync to the cloud
3. **Work offline** - data is stored locally and synced when online
4. **Real-time updates** - changes appear instantly across devices

## 🚀 Ready to Use!

Your BookLedger app is now ready with:
- ✅ User authentication
- ✅ Expense tracking
- ✅ Sales tracking
- ✅ Cloud synchronization
- ✅ Real-time updates
- ✅ Offline support

## 🔍 Troubleshooting

### If PocketBase won't start:
1. Check if port 8090 is available
2. Try running: `cd pocketbase && .\pocketbase.exe serve`

### If collections don't appear:
1. Refresh the admin panel
2. Check the browser console for errors
3. Verify the collection names match exactly

### If Android app can't connect:
1. Ensure PocketBase is running on port 8090
2. Check the server URL in `PocketBaseService.kt`
3. Verify network permissions in AndroidManifest.xml

---

**Your BookLedger app is now fully functional with a powerful backend! 🎉**
