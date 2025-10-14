#!/bin/bash
# Check status of Jenkins Maven Project Web Application

APP_NAME="jenkins-maven-project"
PID_FILE="app.pid"
PORT=4567

echo "========================================"
echo "  $APP_NAME Status"
echo "========================================"
echo ""

# Check PID file
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p "$PID" > /dev/null 2>&1; then
        echo "Status: ✓ RUNNING"
        echo "PID: $PID"
        echo "URL: http://localhost:$PORT"
        echo ""
        
        # Check if port is listening
        if lsof -i :$PORT | grep -q LISTEN; then
            echo "Port $PORT: ✓ LISTENING"
        else
            echo "Port $PORT: ✗ NOT LISTENING (process may be starting)"
        fi
        
        # Show process info
        echo ""
        echo "Process Details:"
        ps -p "$PID" -o pid,ppid,%cpu,%mem,etime,command
        
        # Show recent logs
        if [ -f "app.log" ]; then
            echo ""
            echo "Recent Logs (last 10 lines):"
            echo "----------------------------"
            tail -10 app.log
        fi
    else
        echo "Status: ✗ NOT RUNNING (stale PID file)"
        echo "Cleaning up..."
        rm -f "$PID_FILE"
    fi
else
    echo "Status: ✗ NOT RUNNING"
    echo ""
    
    # Check if port is in use by another process
    if lsof -i :$PORT | grep -q LISTEN; then
        echo "Warning: Port $PORT is in use by another process:"
        lsof -i :$PORT | grep LISTEN
    fi
fi

echo ""
echo "========================================"
echo "Commands:"
echo "  Start:  ./start-app.sh"
echo "  Stop:   ./stop-app.sh"
echo "  Logs:   tail -f app.log"
echo "========================================"
