# Use Ubuntu as base image for better compatibility
FROM ubuntu:22.04

# Set environment variables
ENV DEBIAN_FRONTEND=noninteractive

# Install necessary packages
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Download PocketBase binary using wget (more reliable than curl)
RUN wget -O pocketbase.zip https://github.com/pocketbase/pocketbase/releases/download/v0.22.13/pocketbase_0.22.13_linux_amd64.zip \
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
  CMD wget --quiet --tries=1 --spider http://localhost:8090/api/health || exit 1

# Start PocketBase
CMD ["./pocketbase", "serve", "--http=0.0.0.0:8090"]