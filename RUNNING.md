# Jenkins Maven Project - Application Management

This directory contains scripts and configurations to keep your web application running.

## 🚀 Quick Start

The application is **currently running** on: **http://localhost:4567**

## 📋 Management Commands

### Check Status
```bash
./status-app.sh
```
Shows if the application is running, PID, and recent logs.

### Start Application
```bash
./start-app.sh
```
Starts the application in the background.

### Stop Application
```bash
./stop-app.sh
```
Gracefully stops the application.

### Restart Application
```bash
./restart-app.sh
```
Stops and starts the application.

### View Logs
```bash
tail -f app.log
```
Follow the application logs in real-time.

## 🔄 Keep Application Running

### Option 1: Manual Background Process (Current Method)
The application is running with `nohup`:
- **Pros**: Simple, no configuration needed
- **Cons**: Doesn't auto-restart on crashes or system reboot
- **Logs**: `app.log` and `nohup.out`

### Option 2: macOS launchd Service (Recommended for Production)
Set up as a system service that auto-starts and auto-restarts:

```bash
# Copy service file
cp com.example.jenkins-maven-project.plist ~/Library/LaunchAgents/

# Load the service
launchctl load ~/Library/LaunchAgents/com.example.jenkins-maven-project.plist

# Start the service
launchctl start com.example.jenkins-maven-project

# Check status
launchctl list | grep jenkins-maven-project
```

**Benefits:**
- ✅ Starts automatically on login
- ✅ Auto-restarts if it crashes
- ✅ Proper system integration
- ✅ Easy to manage with launchctl

**To stop the service:**
```bash
launchctl stop com.example.jenkins-maven-project
launchctl unload ~/Library/LaunchAgents/com.example.jenkins-maven-project.plist
```

### Option 3: Docker Container (Best for Production Deployment)
See `Dockerfile` and `docker-compose.yml` for containerized deployment.

## 📊 Monitoring

### Check if application is responding:
```bash
curl http://localhost:4567/health
```

Expected response:
```json
{"status":"healthy","service":"jenkins-maven-project"}
```

### Check application process:
```bash
ps aux | grep jenkins-maven-project
```

### Check port usage:
```bash
lsof -i :4567
```

## 🐛 Troubleshooting

### Application won't start
1. Check if port 4567 is already in use:
   ```bash
   lsof -i :4567
   ```

2. Check the logs:
   ```bash
   cat app.log
   ```

3. Verify JAR file exists:
   ```bash
   ls -lh target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar
   ```

4. Rebuild if needed:
   ```bash
   mvn clean package
   ```

### Application stopped unexpectedly
1. Check logs for errors:
   ```bash
   tail -100 app.log
   ```

2. Check system resources:
   ```bash
   # Memory
   top -l 1 | grep PhysMem
   
   # Disk space
   df -h
   ```

3. Restart the application:
   ```bash
   ./restart-app.sh
   ```

### Multiple instances running
```bash
# Find all instances
ps aux | grep jenkins-maven-project

# Kill specific process
kill <PID>

# Or use the stop script
./stop-app.sh
```

## 🌐 Accessing the Application

### Local Access
- **Web UI**: http://localhost:4567
- **Health Check**: http://localhost:4567/health
- **API Documentation**: http://localhost:4567/api

### API Endpoints
- `GET /` - Interactive calculator UI
- `GET /api/add/:a/:b` - Addition
- `GET /api/subtract/:a/:b` - Subtraction
- `GET /api/multiply/:a/:b` - Multiplication
- `GET /api/divide/:a/:b` - Division
- `GET /health` - Health check

### Examples
```bash
# Addition
curl http://localhost:4567/api/add/10/5

# Health check
curl http://localhost:4567/health

# Open in browser
open http://localhost:4567
```

## 📁 Files

- `start-app.sh` - Start the application
- `stop-app.sh` - Stop the application
- `status-app.sh` - Check application status
- `restart-app.sh` - Restart the application
- `com.example.jenkins-maven-project.plist` - macOS launchd service configuration
- `app.log` - Application logs
- `app.pid` - Process ID file
- `nohup.out` - nohup output (if using nohup directly)

## 🔐 Security Notes

For production deployment:
1. Change the default port if needed (set PORT environment variable)
2. Add authentication/authorization
3. Enable HTTPS
4. Configure firewall rules
5. Set up monitoring and alerting
6. Use a reverse proxy (nginx/Apache)

## 📝 Environment Variables

- `PORT` - Server port (default: 4567)

Example:
```bash
PORT=8080 ./start-app.sh
```

## 🎯 Current Status

**Status**: ✅ **RUNNING**
**PID**: Check with `./status-app.sh`
**URL**: http://localhost:4567
**Started**: $(date)
