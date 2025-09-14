#!/bin/bash

# Deploy BookLedger to Render
# This script helps you deploy your PocketBase backend to Render

set -e

echo "🚀 BookLedger Backend Deployment Script"
echo "======================================"

# Check if git is available
if ! command -v git &> /dev/null; then
    echo "❌ Git is not installed. Please install Git first."
    exit 1
fi

# Check if we're in a git repository
if [ ! -d ".git" ]; then
    echo "❌ Not in a git repository. Please run this from your project root."
    exit 1
fi

# Check if files exist
required_files=("Dockerfile" "render.yaml" "start.sh" "pocketbase/pocketbase-prod.yml")
for file in "${required_files[@]}"; do
    if [ ! -f "$file" ]; then
        echo "❌ Required file $file not found. Please ensure all deployment files are present."
        exit 1
    fi
done

echo "✅ All required files found"

# Generate random secret key if not set
if [ -z "$POCKETBASE_SECRET_KEY" ]; then
    echo "🔑 Generating random secret key..."
    export POCKETBASE_SECRET_KEY=$(openssl rand -hex 32)
    echo "Generated secret key: $POCKETBASE_SECRET_KEY"
    echo "⚠️  IMPORTANT: Save this secret key securely!"
fi

# Generate random admin password if not set
if [ -z "$POCKETBASE_ADMIN_PASSWORD" ]; then
    echo "🔑 Generating random admin password..."
    export POCKETBASE_ADMIN_PASSWORD=$(openssl rand -base64 32)
    echo "Generated admin password: $POCKETBASE_ADMIN_PASSWORD"
    echo "⚠️  IMPORTANT: Save this password securely!"
fi

echo ""
echo "📋 Deployment Checklist:"
echo "========================"
echo "1. ✅ All deployment files present"
echo "2. ✅ Secret key generated: ${POCKETBASE_SECRET_KEY:0:8}..."
echo "3. ✅ Admin password generated: ${POCKETBASE_ADMIN_PASSWORD:0:8}..."
echo ""
echo "📝 Next Steps:"
echo "=============="
echo "1. Push your code to GitHub:"
echo "   git add ."
echo "   git commit -m 'Add PocketBase backend deployment'"
echo "   git push origin main"
echo ""
echo "2. Go to https://dashboard.render.com"
echo "3. Create a new Web Service"
echo "4. Connect your GitHub repository"
echo "5. Use these environment variables:"
echo "   POCKETBASE_ADMIN_EMAIL=admin@bookledger.com"
echo "   POCKETBASE_ADMIN_PASSWORD=$POCKETBASE_ADMIN_PASSWORD"
echo "   POCKETBASE_SECRET_KEY=$POCKETBASE_SECRET_KEY"
echo ""
echo "6. Deploy and test your backend!"
echo ""
echo "🔗 Your backend will be available at: https://bookledger-backend.onrender.com"
echo ""
echo "📚 For detailed instructions, see README-DEPLOYMENT.md"
