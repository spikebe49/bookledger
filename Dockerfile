# Use Alpine Linux as base image
FROM alpine:latest

# Install necessary packages
RUN apk add --no-cache \
    ca-certificates \
    curl \
    unzip \
    && rm -rf /var/cache/apk/*

# Set working directory
WORKDIR /app

# Download PocketBase binary
RUN curl -L https://github.com/pocketbase/pocketbase/releases/latest/download/pocketbase_linux_amd64.zip -o pocketbase.zip \
    && unzip pocketbase.zip \
    && rm pocketbase.zip \
    && chmod +x pocketbase

# Copy configuration files
COPY pocketbase/pb_schema.json /app/pb_schema.json
COPY pocketbase/pb_migrations.json /app/pb_migrations.json
COPY pocketbase/pb_hooks.js /app/pb_hooks.js

# Create data directory
RUN mkdir -p /app/data

# Expose port
EXPOSE 8090

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8090/api/health || exit 1

# Start PocketBase
CMD ["./pocketbase", "serve", "--http=0.0.0.0:8090"]
