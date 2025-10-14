#!/bin/bash
# Start the Jenkins Maven Project Web Application

APP_NAME="jenkins-maven-project"
JAR_FILE="target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar"
LOG_FILE="app.log"
PID_FILE="app.pid"

# Check if already running
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p "$PID" > /dev/null 2>&1; then
        echo "Application is already running (PID: $PID)"
        echo "Access it at: http://localhost:4567"
        exit 0
    else
        echo "Removing stale PID file..."
        rm -f "$PID_FILE"
    fi
fi

# Check if JAR exists
if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file not found: $JAR_FILE"
    echo "Please run: mvn clean package"
    exit 1
fi

# Start the application
echo "Starting $APP_NAME..."
nohup java -jar "$JAR_FILE" > "$LOG_FILE" 2>&1 &
echo $! > "$PID_FILE"

# Wait a bit and verify
sleep 3

if ps -p $(cat "$PID_FILE") > /dev/null 2>&1; then
    echo "✓ Application started successfully!"
    echo "  PID: $(cat $PID_FILE)"
    echo "  Log: $LOG_FILE"
    echo "  URL: http://localhost:4567"
    echo ""
    echo "To stop: ./stop-app.sh"
    echo "To check status: ./status-app.sh"
    echo "To view logs: tail -f $LOG_FILE"
else
    echo "✗ Failed to start application"
    echo "Check logs: cat $LOG_FILE"
    rm -f "$PID_FILE"
    exit 1
fi
