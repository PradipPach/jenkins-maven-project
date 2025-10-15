# EC2 Deployment Guide - Web Application Access Fix

## Problem Solved
The application was binding to `localhost` (127.0.0.1) which only allows local connections. It's now configured to bind to `0.0.0.0` to accept connections from any network interface.

## What Was Changed
- **File**: `src/main/java/com/example/WebApp.java`
- **Change**: Added `ipAddress("0.0.0.0");` before setting the port
- **Result**: Application now accepts external connections

## Deployment Steps on EC2

### 1. Rebuild and Redeploy
After this fix, you need to rebuild your application:

```bash
# On your Jenkins server or EC2 instance
cd /path/to/jenkins-maven-project

# Rebuild the project
mvn clean package

# Or trigger the Jenkins pipeline again
```

### 2. Stop the Current Application
```bash
# Find the running Java process
ps aux | grep jenkins-maven-project

# Kill the process (replace <PID> with actual process ID)
kill <PID>

# Or if using the stop script
./stop-app.sh
```

### 3. Start the Updated Application
```bash
# Option 1: Run with the start script
./start-app.sh

# Option 2: Run directly
java -jar target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar

# Option 3: Run with Docker (rebuild image first)
docker build -t jenkins-maven-project .
docker run -d -p 5000:5000 --name my-app jenkins-maven-project

# Option 4: Run with Docker Compose
docker-compose down
docker-compose up -d --build
```

### 4. Verify It's Listening on All Interfaces
```bash
# Check if the app is listening on 0.0.0.0
sudo netstat -tlnp | grep 5000
# Should show: 0.0.0.0:5000 (not 127.0.0.1:5000)

# Alternative with ss command
sudo ss -tlnp | grep 5000

# Test local access
curl http://localhost:5000
curl http://localhost:5000/health
```

## AWS Security Group Configuration

### Inbound Rules Required
You mentioned you added port 5000 - verify it's configured correctly:

| Type | Protocol | Port Range | Source | Description |
|------|----------|------------|--------|-------------|
| Custom TCP | TCP | 5000 | 0.0.0.0/0 | Web application (public) |
| Custom TCP | TCP | 5000 | ::/0 | Web application (IPv6) |

**Security Best Practice**: Consider restricting source to your IP address or VPN CIDR instead of 0.0.0.0/0

### How to Verify Security Group
1. Go to EC2 Console
2. Select your instance
3. Click on **Security** tab
4. Click on the security group name
5. Check **Inbound rules**
6. Ensure port 5000 is open with TCP protocol

## Network ACLs (If Issues Persist)
Check if Network ACLs are blocking traffic:

1. Go to VPC Console
2. Select **Network ACLs**
3. Find the ACL associated with your subnet
4. Check both **Inbound** and **Outbound** rules
5. Ensure port 5000 is allowed (or use default which allows all)

## Accessing Your Application

### Get Your EC2 Public IP
```bash
# From within EC2 instance
curl http://169.254.169.254/latest/meta-data/public-ipv4

# Or from AWS Console
# EC2 Dashboard → Instances → Select Instance → Copy "Public IPv4 address"
```

### Access URLs
Replace `<EC2-PUBLIC-IP>` with your actual EC2 public IP address:

- **Homepage**: `http://<EC2-PUBLIC-IP>:5000`
- **Health Check**: `http://<EC2-PUBLIC-IP>:5000/health`
- **API Endpoints**: `http://<EC2-PUBLIC-IP>:5000/api/...`

**Example**: If your public IP is `54.123.45.67`:
- Access at: `http://54.123.45.67:5000`

## Troubleshooting Checklist

### ✅ Application Level
- [ ] Application is rebuilt with the 0.0.0.0 binding fix
- [ ] Application is running (check with `ps aux | grep java`)
- [ ] Application is listening on 0.0.0.0:5000 (check with `netstat`)
- [ ] Application responds to local curl requests

### ✅ EC2 Instance Level
- [ ] EC2 instance is running
- [ ] Public IP address is assigned
- [ ] OS firewall allows port 5000 (see below)

### ✅ AWS Network Level
- [ ] Security Group has inbound rule for port 5000
- [ ] Network ACL allows inbound/outbound on port 5000
- [ ] Instance is in a public subnet (has route to IGW)
- [ ] Route table has 0.0.0.0/0 → Internet Gateway

### ✅ Browser/Client Level
- [ ] Using http:// not https://
- [ ] Using public IP, not private IP
- [ ] Using correct port :5000
- [ ] No VPN/proxy blocking the connection

## EC2 OS Firewall Check

### For Amazon Linux / RHEL / CentOS
```bash
# Check if firewalld is running
sudo systemctl status firewalld

# If running, allow port 5000
sudo firewall-cmd --permanent --add-port=5000/tcp
sudo firewall-cmd --reload

# Or disable firewalld (not recommended for production)
sudo systemctl stop firewalld
sudo systemctl disable firewalld
```

### For Ubuntu / Debian
```bash
# Check UFW status
sudo ufw status

# If active, allow port 5000
sudo ufw allow 5000/tcp
sudo ufw reload

# Or disable UFW (not recommended for production)
sudo ufw disable
```

## Testing Connectivity

### From EC2 Instance (Local)
```bash
# Test if app is running
curl http://localhost:5000
curl http://localhost:5000/health

# Check listening ports
sudo netstat -tlnp | grep 5000
sudo ss -tlnp | grep 5000
```

### From Your Local Machine
```bash
# Test connectivity to EC2
ping <EC2-PUBLIC-IP>

# Test port 5000 (if telnet is installed)
telnet <EC2-PUBLIC-IP> 5000

# Test with curl
curl http://<EC2-PUBLIC-IP>:5000
curl http://<EC2-PUBLIC-IP>:5000/health

# Test with wget
wget http://<EC2-PUBLIC-IP>:5000
```

### Using Online Tools
If local tests don't work, try these online port checkers:
- https://www.yougetsignal.com/tools/open-ports/
- https://ping.eu/port-chk/

## Common Errors and Solutions

### Error: "Connection Refused"
**Cause**: Application not running or not listening on correct interface
**Solution**:
1. Verify app is running: `ps aux | grep java`
2. Check binding: `sudo netstat -tlnp | grep 5000`
3. Should show `0.0.0.0:5000`, not `127.0.0.1:5000`

### Error: "Connection Timeout"
**Cause**: Firewall (Security Group, Network ACL, or OS firewall) blocking
**Solution**:
1. Check Security Group inbound rules
2. Check Network ACL rules
3. Check OS firewall (iptables, firewalld, ufw)

### Error: "No Route to Host"
**Cause**: Network configuration issue
**Solution**:
1. Verify instance has public IP
2. Check route table has IGW route
3. Verify instance is in public subnet

### Application Logs Show "Address Already in Use"
**Cause**: Port 5000 is already in use
**Solution**:
```bash
# Find what's using port 5000
sudo lsof -i :5000
sudo netstat -tlnp | grep 5000

# Kill the process or change your app port
```

## Production Recommendations

### 1. Use HTTPS with SSL/TLS
```bash
# Option 1: Use AWS Application Load Balancer (ALB)
# - ALB handles SSL termination
# - Point to your EC2 instance on port 5000
# - Configure SSL certificate via ACM

# Option 2: Use Nginx as reverse proxy
sudo yum install nginx -y  # Amazon Linux
sudo apt install nginx -y   # Ubuntu

# Configure Nginx to proxy to your app
# Add SSL certificate (Let's Encrypt)
```

### 2. Use Standard Ports
Consider using port 80 (HTTP) or 443 (HTTPS) with a reverse proxy:
- Nginx or Apache as reverse proxy
- ALB/ELB in front of EC2
- CloudFront for CDN and SSL

### 3. Restrict Security Group
Don't use 0.0.0.0/0 in production:
```
# Instead, use:
- Your office IP/CIDR
- VPN IP range
- CloudFront IP ranges
- ALB security group
```

### 4. Monitor Your Application
```bash
# Use CloudWatch for monitoring
# Set up alarms for:
- CPU utilization
- Network in/out
- Status checks
- Application logs
```

### 5. Auto-Restart on Failure
Create a systemd service:
```bash
sudo nano /etc/systemd/system/webapp.service
```

```ini
[Unit]
Description=Jenkins Maven Project Web App
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user/jenkins-maven-project
ExecStart=/usr/bin/java -jar target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable webapp
sudo systemctl start webapp
sudo systemctl status webapp
```

## Quick Recovery Commands

```bash
# Complete rebuild and restart
cd /path/to/jenkins-maven-project
./stop-app.sh
mvn clean package
./start-app.sh

# Check status
./status-app.sh

# View logs
tail -f nohup.out
```

## Need Help?

1. **Check Application Logs**: `cat nohup.out` or `docker logs <container>`
2. **Check System Logs**: `sudo journalctl -u webapp` or `/var/log/messages`
3. **AWS Support**: Check AWS Console for instance status checks
4. **Network Diagnostics**: Use VPC Reachability Analyzer

## Summary

The key fix applied:
```java
// Before (only local access)
port(getPort());

// After (external access allowed)
ipAddress("0.0.0.0");  // ← This line was added
port(getPort());
```

After rebuilding and redeploying, your application should be accessible from the internet at `http://<EC2-PUBLIC-IP>:5000`
