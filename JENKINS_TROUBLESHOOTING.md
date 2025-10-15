# Jenkins Pipeline Troubleshooting Guide

## Overview
This guide helps you debug and resolve common issues with the Jenkins Maven Project pipeline.

---

## Table of Contents
1. [Pipeline Configuration](#pipeline-configuration)
2. [Common Issues](#common-issues)
3. [Debugging Tools](#debugging-tools)
4. [Build Failures](#build-failures)
5. [Docker Issues](#docker-issues)
6. [Deployment Problems](#deployment-problems)
7. [Performance Optimization](#performance-optimization)

---

## Pipeline Configuration

### Prerequisites
Before running the pipeline, ensure Jenkins has:

1. **Java 21 LTS** installed and configured
   - Tool name in Jenkins: `myJDK`
   - Path: Configure in Global Tool Configuration

2. **Maven 3.9.x** installed and configured
   - Tool name in Jenkins: `myMaven`
   - Path: Configure in Global Tool Configuration

3. **Docker** (optional, for container builds)
   - Ensure Jenkins user has Docker permissions
   - Test: `docker ps` from Jenkins shell

### Jenkins Configuration Steps

1. **Configure JDK:**
   ```
   Manage Jenkins → Global Tool Configuration → JDK
   Name: myJDK
   Install automatically: Yes (or specify JAVA_HOME)
   Version: Java 21
   ```

2. **Configure Maven:**
   ```
   Manage Jenkins → Global Tool Configuration → Maven
   Name: myMaven
   Install automatically: Yes (or specify Maven path)
   Version: 3.9.11 or later
   ```

3. **Add GitHub/Git Credentials:**
   ```
   Manage Jenkins → Credentials → System → Global credentials
   Add credentials for your Git repository
   ```

4. **Create Pipeline Job:**
   ```
   New Item → Pipeline
   Pipeline definition: Pipeline script from SCM
   SCM: Git
   Repository URL: https://github.com/atulkamble/jenkins-maven-project
   Branch: main
   Script Path: Jenkinsfile
   ```

---

## Common Issues

### Issue 1: "Tool 'myMaven' does not exist"

**Symptom:**
```
ERROR: No such tool 'myMaven'
```

**Solution:**
1. Go to `Manage Jenkins` → `Global Tool Configuration`
2. Add Maven installation with name exactly as `myMaven`
3. OR change the name in Jenkinsfile to match your Maven installation name

### Issue 2: "Tool 'myJDK' does not exist"

**Symptom:**
```
ERROR: No such tool 'myJDK'
```

**Solution:**
1. Go to `Manage Jenkins` → `Global Tool Configuration`
2. Add JDK installation with name exactly as `myJDK`
3. Ensure it's Java 21 LTS
4. OR change the name in Jenkinsfile to match your JDK installation name

### Issue 3: Compilation Errors

**Symptom:**
```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin
```

**Solutions:**
1. **Check Java version:**
   ```bash
   java -version  # Should be Java 21
   ```

2. **Clean and rebuild:**
   ```bash
   mvn clean compile
   ```

3. **Check pom.xml for correct Java version:**
   ```xml
   <maven.compiler.source>21</maven.compiler.source>
   <maven.compiler.target>21</maven.compiler.target>
   ```

### Issue 4: Test Failures

**Symptom:**
```
Tests run: 5, Failures: 1, Errors: 0, Skipped: 0
```

**Solutions:**
1. **Review test reports:**
   ```bash
   cat target/surefire-reports/*.txt
   ```

2. **Run tests locally:**
   ```bash
   mvn test
   ```

3. **Check specific test:**
   ```bash
   mvn test -Dtest=CalculatorTest
   ```

4. **Skip tests temporarily (not recommended):**
   ```bash
   mvn package -DskipTests
   ```

### Issue 5: Docker Build Failures

**Symptom:**
```
Cannot connect to the Docker daemon
```

**Solutions:**
1. **Check Docker daemon:**
   ```bash
   docker ps
   systemctl status docker  # Linux
   ```

2. **Add Jenkins user to docker group:**
   ```bash
   sudo usermod -aG docker jenkins
   sudo systemctl restart jenkins
   ```

3. **Test Docker access:**
   ```bash
   docker run hello-world
   ```

### Issue 6: Port Already in Use

**Symptom:**
```
Address already in use: bind
```

**Solutions:**
1. **Find process using port 5000:**
   ```bash
   # macOS/Linux
   lsof -i :5000
   
   # Windows
   netstat -ano | findstr :5000
   ```

2. **Kill the process:**
   ```bash
   kill -9 <PID>
   ```

3. **Change port in code:**
   - Edit `src/main/java/com/example/WebApp.java`
   - Change `getPort()` method to return different port

---

## Debugging Tools

### 1. Debug Script
Run the automated debug script:
```bash
./debug-pipeline.sh
```

This script checks:
- Java and Maven installation
- Project structure
- Dependencies
- Compilation
- Tests
- Docker setup
- Port availability

### 2. Maven Debug Mode
Run Maven with debug output:
```bash
mvn clean package -X
```

### 3. Check Build Artifacts
```bash
# List all generated JARs
ls -lh target/*.jar

# Verify JAR contents
jar -tf target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar | head -20

# Check manifest
unzip -p target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar META-INF/MANIFEST.MF
```

### 4. Test Application Locally
```bash
# Build the project
mvn clean package

# Run the application
java -jar target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar

# Test endpoints
curl http://localhost:5000
curl http://localhost:5000/health
curl "http://localhost:5000/calculate/add?a=5&b=3"
```

---

## Build Failures

### Stage: Checkout
**Problem:** Cannot access repository

**Solutions:**
1. Check Git credentials in Jenkins
2. Verify repository URL
3. Check network connectivity
4. Ensure branch name is correct (main vs master)

### Stage: Compile
**Problem:** Compilation errors

**Solutions:**
1. Verify Java 21 is being used
2. Check for syntax errors in code
3. Ensure all dependencies are available
4. Clean Maven cache: `mvn dependency:purge-local-repository`

### Stage: Test
**Problem:** Tests failing

**Solutions:**
1. Review test output in Jenkins console
2. Check test reports: `target/surefire-reports/`
3. Run tests locally with same Java version
4. Check for environment-specific issues

### Stage: Package
**Problem:** JAR creation fails

**Solutions:**
1. Check Maven Shade plugin configuration
2. Verify main class is correct
3. Check for dependency conflicts
4. Review Maven logs for errors

---

## Docker Issues

### Image Build Failures

1. **Check Dockerfile syntax:**
   ```bash
   docker build -t jenkins-maven-project:test .
   ```

2. **Verify base image:**
   - Current: `maven:3.9.9-eclipse-temurin-21`
   - Alternative: `eclipse-temurin:21-jdk`

3. **Build without cache:**
   ```bash
   docker build --no-cache -t jenkins-maven-project:test .
   ```

### Security Vulnerabilities

**Check for vulnerabilities:**
```bash
# Using Trivy
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  aquasec/trivy image jenkins-maven-project:latest

# Using Docker Scout
docker scout cves jenkins-maven-project:latest
```

**Solutions:**
1. Update base images regularly
2. Use minimal images (alpine variants)
3. Remove unnecessary packages
4. Scan images in pipeline

---

## Deployment Problems

### Cannot Access Application

1. **Check if application is running:**
   ```bash
   ps aux | grep java
   ```

2. **Verify port binding:**
   ```bash
   netstat -tulpn | grep 5000
   ```

3. **Check application logs:**
   ```bash
   tail -f app.log
   ```

4. **Test locally:**
   ```bash
   curl -v http://localhost:5000/health
   ```

### Docker Container Issues

1. **Check container status:**
   ```bash
   docker ps -a
   ```

2. **View container logs:**
   ```bash
   docker logs jenkins-maven-project
   ```

3. **Inspect container:**
   ```bash
   docker inspect jenkins-maven-project
   ```

4. **Access container shell:**
   ```bash
   docker exec -it jenkins-maven-project sh
   ```

---

## Performance Optimization

### Speed Up Builds

1. **Use Maven daemon:**
   ```bash
   mvnd clean package  # If mvnd is installed
   ```

2. **Parallel builds:**
   ```bash
   mvn -T 4 clean package  # Use 4 threads
   ```

3. **Skip tests in development:**
   ```bash
   mvn package -DskipTests
   ```

4. **Use build cache:**
   - Configure Maven local repository
   - Use Docker layer caching

### Reduce Build Time

1. **Optimize Dockerfile:**
   - Use multi-stage builds
   - Leverage layer caching
   - Copy only necessary files

2. **Maven optimizations:**
   ```xml
   <properties>
       <maven.test.skip>false</maven.test.skip>
       <skipITs>true</skipITs>
   </properties>
   ```

3. **Jenkins optimizations:**
   - Use lightweight checkout
   - Implement build timeout
   - Archive only necessary artifacts

---

## Pipeline Stages Explained

### 1. Checkout
- Pulls code from Git repository
- Records commit ID for traceability

### 2. Build Info
- Displays environment information
- Verifies Java and Maven versions
- Checks Docker availability

### 3. Dependency Check
- Resolves Maven dependencies
- Checks for dependency updates
- Reports potential vulnerabilities

### 4. Clean
- Removes previous build artifacts
- Ensures clean build environment

### 5. Compile
- Compiles Java source code
- Generates class files in target/

### 6. Test
- Runs JUnit tests
- Generates test reports
- Publishes results to Jenkins

### 7. Code Analysis
- Checks for TODO/FIXME comments
- Provides code statistics
- Can integrate SonarQube

### 8. Package
- Creates JAR files
- Builds standalone executable JAR
- Uses Maven Shade plugin

### 9. Archive Artifacts
- Stores build artifacts in Jenkins
- Enables fingerprinting for traceability
- Makes JARs downloadable

### 10. Docker Build (Optional)
- Builds Docker image
- Tags with build number and latest
- Only runs on main branch

### 11. Docker Security Scan (Optional)
- Scans image for vulnerabilities
- Uses Trivy or similar tools
- Reports security issues

### 12. Deploy
- Simulates deployment process
- Provides deployment options
- Can be customized for actual deployment

---

## Useful Commands

### Maven Commands
```bash
# Full build
mvn clean install

# Skip tests
mvn clean install -DskipTests

# Run specific test
mvn test -Dtest=CalculatorTest

# Dependency tree
mvn dependency:tree

# Update dependencies
mvn versions:use-latest-releases

# Verify checksums
mvn verify -Dchecksum.verify
```

### Docker Commands
```bash
# Build image
docker build -t jenkins-maven-project:latest .

# Run container
docker run -d -p 5000:5000 --name myapp jenkins-maven-project:latest

# View logs
docker logs -f myapp

# Stop container
docker stop myapp

# Remove container
docker rm myapp

# Clean up
docker system prune -a
```

### Jenkins CLI Commands
```bash
# Trigger build
java -jar jenkins-cli.jar -s http://jenkins-server/ build job-name

# Get build log
java -jar jenkins-cli.jar -s http://jenkins-server/ console job-name

# List jobs
java -jar jenkins-cli.jar -s http://jenkins-server/ list-jobs
```

---

## Getting Help

1. **Check Jenkins Console Output:**
   - Full build logs available in Jenkins UI
   - Look for ERROR or WARNING messages

2. **Review Documentation:**
   - [Jenkins Documentation](https://www.jenkins.io/doc/)
   - [Maven Documentation](https://maven.apache.org/guides/)
   - [Docker Documentation](https://docs.docker.com/)

3. **Common Resources:**
   - GitHub Issues: https://github.com/atulkamble/jenkins-maven-project/issues
   - Stack Overflow: Tag with `jenkins`, `maven`, `java`
   - Jenkins Community: https://community.jenkins.io/

4. **Debug Locally:**
   - Always test locally before pushing to Jenkins
   - Use the debug script: `./debug-pipeline.sh`
   - Check application logs: `app.log`

---

## Monitoring and Alerts

### Set Up Monitoring

1. **Jenkins Build Notifications:**
   - Email notifications on failure
   - Slack/Teams integration
   - Build status badges

2. **Application Monitoring:**
   - Health check endpoint: `/health`
   - Application metrics
   - Log aggregation

3. **Docker Monitoring:**
   - Container health checks
   - Resource usage monitoring
   - Log collection

---

## Best Practices

1. ✅ Always run tests before committing
2. ✅ Use semantic versioning for releases
3. ✅ Keep dependencies up to date
4. ✅ Review security scan results
5. ✅ Document configuration changes
6. ✅ Use environment-specific configurations
7. ✅ Implement proper error handling
8. ✅ Monitor build times and optimize
9. ✅ Keep the workspace clean
10. ✅ Use descriptive commit messages

---

## Emergency Recovery

### Build is Stuck
```bash
# Stop the build in Jenkins UI
# Clean workspace
rm -rf /path/to/jenkins/workspace/job-name/*

# Restart Jenkins if needed
sudo systemctl restart jenkins
```

### Corrupted Maven Repository
```bash
# Clean local Maven repository
rm -rf ~/.m2/repository

# Re-download dependencies
mvn dependency:resolve
```

### Reset Everything
```bash
# Clean all build artifacts
mvn clean

# Remove Docker containers and images
docker stop $(docker ps -aq)
docker rm $(docker ps -aq)
docker rmi $(docker images -q)

# Rebuild from scratch
mvn clean install
docker build -t jenkins-maven-project:latest .
```

---

## Version History

- **v1.0** - Initial Jenkins pipeline setup
- **v1.1** - Added Java 21 support
- **v1.2** - Enhanced with Docker build stages
- **v1.3** - Added security scanning and improved error handling
- **v1.4** - Current version with comprehensive debugging support

---

**Last Updated:** October 15, 2025  
**Maintained by:** Atul Kamble  
**Repository:** https://github.com/atulkamble/jenkins-maven-project
