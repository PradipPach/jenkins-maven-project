# Docker Deployment Guide

## Build and Run with Docker

### Build the Docker image
```bash
docker build -t jenkins-maven-project:latest .
```

### Run the container
```bash
docker run -d \
  --name jenkins-maven-project \
  -p 4567:4567 \
  --restart unless-stopped \
  jenkins-maven-project:latest
```

### Check container status
```bash
docker ps
docker logs jenkins-maven-project
```

### Stop the container
```bash
docker stop jenkins-maven-project
docker rm jenkins-maven-project
```

## Using Docker Compose (Recommended)

### Start the application
```bash
docker-compose up -d
```

### View logs
```bash
docker-compose logs -f
```

### Stop the application
```bash
docker-compose down
```

### Rebuild and restart
```bash
docker-compose up -d --build
```

## Access the Application

Once running in Docker:
- **URL**: http://localhost:4567
- **Health Check**: http://localhost:4567/health

## Docker Commands Reference

```bash
# View running containers
docker ps

# View all containers
docker ps -a

# View container logs
docker logs jenkins-maven-project
docker logs -f jenkins-maven-project  # Follow logs

# Execute command in container
docker exec -it jenkins-maven-project sh

# Inspect container
docker inspect jenkins-maven-project

# View container resource usage
docker stats jenkins-maven-project

# Remove image
docker rmi jenkins-maven-project:latest
```

## Kubernetes Deployment

See `k8s/` directory for Kubernetes manifests.
