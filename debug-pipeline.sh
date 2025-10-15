#!/bin/bash

# Jenkins Maven Project - Debug & Troubleshooting Script
# This script helps debug common issues with the Jenkins pipeline

set -e

echo "================================================"
echo "Jenkins Maven Project - Debug Script"
echo "================================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to check command availability
check_command() {
    if command -v "$1" &> /dev/null; then
        echo -e "${GREEN}✓${NC} $1 is installed"
        $1 --version 2>&1 | head -n 1
        return 0
    else
        echo -e "${RED}✗${NC} $1 is NOT installed"
        return 1
    fi
}

# Function to run tests with detailed output
run_test() {
    echo ""
    echo "Running: $1"
    echo "----------------------------------------"
    if eval "$1"; then
        echo -e "${GREEN}✓ Test passed${NC}"
        return 0
    else
        echo -e "${RED}✗ Test failed${NC}"
        return 1
    fi
}

echo "1. Checking Required Tools"
echo "----------------------------------------"
check_command java
check_command mvn
check_command docker
check_command git

echo ""
echo "2. Java Environment"
echo "----------------------------------------"
echo "JAVA_HOME: ${JAVA_HOME:-Not set}"
echo "Java version:"
java -version 2>&1
echo ""
echo "Maven version:"
mvn --version

echo ""
echo "3. Project Structure Check"
echo "----------------------------------------"
if [ -f "pom.xml" ]; then
    echo -e "${GREEN}✓${NC} pom.xml found"
else
    echo -e "${RED}✗${NC} pom.xml not found"
    exit 1
fi

if [ -f "Jenkinsfile" ]; then
    echo -e "${GREEN}✓${NC} Jenkinsfile found"
else
    echo -e "${RED}✗${NC} Jenkinsfile not found"
fi

if [ -d "src/main/java" ]; then
    echo -e "${GREEN}✓${NC} Source directory found"
    echo "Java files: $(find src/main/java -name '*.java' | wc -l)"
else
    echo -e "${RED}✗${NC} Source directory not found"
fi

if [ -d "src/test/java" ]; then
    echo -e "${GREEN}✓${NC} Test directory found"
    echo "Test files: $(find src/test/java -name '*.java' | wc -l)"
else
    echo -e "${YELLOW}⚠${NC} Test directory not found"
fi

echo ""
echo "4. Dependency Check"
echo "----------------------------------------"
echo "Checking Maven dependencies..."
mvn dependency:resolve > /dev/null 2>&1 && echo -e "${GREEN}✓${NC} All dependencies resolved" || echo -e "${RED}✗${NC} Dependency resolution failed"

echo ""
echo "5. Compilation Test"
echo "----------------------------------------"
run_test "mvn clean compile"

echo ""
echo "6. Unit Tests"
echo "----------------------------------------"
run_test "mvn test"

echo ""
echo "7. Package Build"
echo "----------------------------------------"
run_test "mvn package -DskipTests"

echo ""
echo "8. JAR File Verification"
echo "----------------------------------------"
if [ -f "target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar" ]; then
    echo -e "${GREEN}✓${NC} Standalone JAR created"
    echo "File size: $(ls -lh target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar | awk '{print $5}')"
    echo ""
    echo "Main class check:"
    jar -xf target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar META-INF/MANIFEST.MF 2>/dev/null
    if [ -f "META-INF/MANIFEST.MF" ]; then
        grep "Main-Class" META-INF/MANIFEST.MF || echo "Main-Class not found in manifest"
        rm -rf META-INF
    fi
else
    echo -e "${RED}✗${NC} Standalone JAR not found"
fi

echo ""
echo "9. Docker Check"
echo "----------------------------------------"
if command -v docker &> /dev/null; then
    if docker info &> /dev/null; then
        echo -e "${GREEN}✓${NC} Docker daemon is running"
        
        echo ""
        echo "Building Docker image..."
        if docker build -t jenkins-maven-project:debug . > /dev/null 2>&1; then
            echo -e "${GREEN}✓${NC} Docker image built successfully"
            echo "Image size: $(docker images jenkins-maven-project:debug --format '{{.Size}}')"
        else
            echo -e "${RED}✗${NC} Docker build failed"
        fi
    else
        echo -e "${RED}✗${NC} Docker daemon is not running"
    fi
else
    echo -e "${YELLOW}⚠${NC} Docker not installed"
fi

echo ""
echo "10. Port Availability Check"
echo "----------------------------------------"
PORT=5000
if lsof -Pi :$PORT -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo -e "${YELLOW}⚠${NC} Port $PORT is already in use"
    echo "Process using port $PORT:"
    lsof -Pi :$PORT -sTCP:LISTEN 2>/dev/null || netstat -an | grep $PORT
else
    echo -e "${GREEN}✓${NC} Port $PORT is available"
fi

echo ""
echo "11. Code Quality Checks"
echo "----------------------------------------"
echo "Checking for common issues..."
TODO_COUNT=$(grep -r "TODO" src/ 2>/dev/null | wc -l || echo 0)
FIXME_COUNT=$(grep -r "FIXME" src/ 2>/dev/null | wc -l || echo 0)
echo "TODO comments: $TODO_COUNT"
echo "FIXME comments: $FIXME_COUNT"

echo ""
echo "12. Test Coverage Summary"
echo "----------------------------------------"
if [ -d "target/surefire-reports" ]; then
    echo "Test reports found:"
    ls -1 target/surefire-reports/*.txt 2>/dev/null | while read file; do
        echo ""
        echo "File: $(basename $file)"
        cat "$file" | head -5
    done
else
    echo -e "${YELLOW}⚠${NC} No test reports found. Run 'mvn test' first."
fi

echo ""
echo "13. Disk Space Check"
echo "----------------------------------------"
echo "Current directory size:"
du -sh . 2>/dev/null || echo "Unable to calculate size"
echo ""
echo "Target directory size:"
du -sh target/ 2>/dev/null || echo "Target directory not found"

echo ""
echo "14. Git Status"
echo "----------------------------------------"
if [ -d ".git" ]; then
    echo "Current branch: $(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo 'unknown')"
    echo "Last commit: $(git log -1 --oneline 2>/dev/null || echo 'unknown')"
    echo "Uncommitted changes:"
    git status --short 2>/dev/null || echo "No git repository"
else
    echo -e "${YELLOW}⚠${NC} Not a git repository"
fi

echo ""
echo "================================================"
echo "Debug Summary"
echo "================================================"
echo ""
echo "If all checks passed, your pipeline should work correctly."
echo ""
echo "Common Issues and Solutions:"
echo "1. Compilation errors -> Check Java version (should be 21)"
echo "2. Test failures -> Review test reports in target/surefire-reports/"
echo "3. Docker issues -> Ensure Docker daemon is running"
echo "4. Port conflicts -> Stop services using port 5000"
echo "5. Dependency issues -> Run 'mvn dependency:purge-local-repository'"
echo ""
echo "For manual testing:"
echo "  mvn clean package"
echo "  java -jar target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar"
echo "  Open: http://localhost:5000"
echo ""
echo "Debug script completed!"
echo "================================================"
