# Use the official PocketBase image
FROM ghcr.io/pocketbase/pocketbase:latest

# Set working directory
WORKDIR /app

# Copy PocketBase binary and configuration
COPY pocketbase/pocketbase /app/pocketbase
COPY pocketbase/pb_schema.json /app/pb_schema.json
COPY pocketbase/pb_migrations.json /app/pb_migrations.json
COPY pocketbase/pb_hooks.js /app/pb_hooks.js

# Create data directory
RUN mkdir -p /app/data

# Set permissions
RUN chmod +x /app/pocketbase

# Expose port
EXPOSE 8090

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8090/api/health || exit 1

# Start PocketBase
CMD ["./pocketbase", "serve", "--http=0.0.0.0:8090"]
