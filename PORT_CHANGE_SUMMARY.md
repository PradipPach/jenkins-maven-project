# Port Configuration Change - Port 5000

## Summary

Successfully modified the Jenkins Maven Project to run on **port 5000** instead of the default port 4567.

## Files Updated

### 1. Source Code
- **`src/main/java/com/example/WebApp.java`**
  - Changed default port from 4567 to 5000
  - Updated comment to reflect new default port

### 2. Docker Configuration
- **`Dockerfile`**
  - Updated EXPOSE directive: `4567` → `5000`
  - Updated health check URL: `http://localhost:4567` → `http://localhost:5000`

- **`docker-compose.yml`**
  - Updated port mapping: `"4567:4567"` → `"5000:5000"`
  - Updated PORT environment variable: `4567` → `5000`
  - Updated health check URL: `http://localhost:4567` → `http://localhost:5000`

### 3. Management Scripts
- **`start-app.sh`**
  - Updated URL references from 4567 to 5000

- **`status-app.sh`**
  - Updated PORT variable from 4567 to 5000

### 4. Documentation
- **`README.md`**
  - Updated all API endpoint URLs from port 4567 to 5000
  - Updated health check URL

- **`RUNNING.md`**
  - Updated quick start URL
  - Updated monitoring examples
  - Updated troubleshooting commands
  - Updated API access URLs
  - Updated default port in environment variables section

- **`DOCKER.md`**
  - Updated Docker run command port mapping
  - Updated access URLs

### 5. CI/CD Configuration
- **`Jenkinsfile`**
  - Updated deployment message with new port 5000

### 6. macOS Service Configuration
- **`com.example.jenkins-maven-project.plist`**
  - Updated PORT environment variable from 4567 to 5000

## How to Use

### Run Locally
```bash
# Build the project
mvn clean package

# Start the application (uses port 5000)
./start-app.sh

# Or run directly
java -jar target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar
```

### Access the Application
- **Home Page**: http://localhost:5000
- **Health Check**: http://localhost:5000/health
- **Calculator API**:
  - Add: http://localhost:5000/api/add/10/5
  - Subtract: http://localhost:5000/api/subtract/20/8
  - Multiply: http://localhost:5000/api/multiply/6/7
  - Divide: http://localhost:5000/api/divide/50/5

### Docker Deployment
```bash
# Using Docker
docker build -t jenkins-maven-project:latest .
docker run -d -p 5000:5000 jenkins-maven-project:latest

# Using Docker Compose
docker-compose up -d
```

### Custom Port Override
You can still override the port using the PORT environment variable:
```bash
# Run on port 8080
PORT=8080 java -jar target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar

# Or with the start script
PORT=8080 ./start-app.sh
```

## Verification

### Build Status
✅ **BUILD SUCCESS** - Project rebuilt successfully with port 5000 configuration

### Testing Checklist
- [ ] Start application and verify it listens on port 5000
- [ ] Test health check endpoint: `curl http://localhost:5000/health`
- [ ] Test calculator API: `curl http://localhost:5000/api/add/10/5`
- [ ] Verify web UI loads at http://localhost:5000
- [ ] Test Docker build and run
- [ ] Test Docker Compose deployment
- [ ] Verify management scripts work correctly

### Quick Test Commands
```bash
# Build and run
mvn clean package && java -jar target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar

# In another terminal, test the endpoints
curl http://localhost:5000/health
curl http://localhost:5000/api/add/15/25

# Check if port is listening
lsof -i :5000
```

## Benefits of Port 5000

1. **Common Development Port** - Port 5000 is commonly used for development servers
2. **Flask Convention** - Matches the default port for Flask and other Python frameworks
3. **Easier to Remember** - Simpler port number than 4567
4. **No Conflicts** - Avoids potential conflicts with Spark's default port

## Rollback

If you need to revert to port 4567, simply change:
- `WebApp.java`: Return value from 5000 to 4567
- All configuration files: Replace 5000 with 4567
- Rebuild: `mvn clean package`

## Notes

- The application still respects the `PORT` environment variable for custom port configuration
- All health checks and monitoring scripts have been updated
- Docker configurations now properly expose and map port 5000
- macOS launchd service configuration updated for system service deployment

---
**Configuration Date**: October 15, 2025  
**Previous Port**: 4567  
**New Port**: 5000  
**Status**: ✅ Complete and Ready to Deploy
