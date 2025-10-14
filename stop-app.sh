#!/bin/bash
# Stop the Jenkins Maven Project Web Application

APP_NAME="jenkins-maven-project"
PID_FILE="app.pid"

if [ ! -f "$PID_FILE" ]; then
    echo "Application is not running (no PID file found)"
    exit 0
fi

PID=$(cat "$PID_FILE")

if ! ps -p "$PID" > /dev/null 2>&1; then
    echo "Application is not running (stale PID file)"
    rm -f "$PID_FILE"
    exit 0
fi

echo "Stopping $APP_NAME (PID: $PID)..."
kill "$PID"

# Wait for graceful shutdown
for i in {1..10}; do
    if ! ps -p "$PID" > /dev/null 2>&1; then
        echo "✓ Application stopped successfully"
        rm -f "$PID_FILE"
        exit 0
    fi
    sleep 1
done

# Force kill if still running
if ps -p "$PID" > /dev/null 2>&1; then
    echo "Force killing application..."
    kill -9 "$PID"
    sleep 1
fi

rm -f "$PID_FILE"
echo "✓ Application stopped"
