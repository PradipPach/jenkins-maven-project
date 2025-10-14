#!/bin/bash
# Restart the Jenkins Maven Project Web Application

echo "Restarting application..."
./stop-app.sh
sleep 2
./start-app.sh
