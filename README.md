# Jenkins Maven Pipeline Project

This is a sample Maven project configured with Jenkins Pipeline for CI/CD.

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/atulkamble/jenkins-maven-project.git
cd jenkins-maven-project
```

## Project Structure

```
jenkins-maven-project/
├── pom.xml                           # Maven configuration
├── Jenkinsfile                       # Jenkins Pipeline definition
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── example/
│   │               ├── App.java           # Main application class
│   │               └── Calculator.java    # Calculator utility class
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── CalculatorTest.java # Unit tests
└── README.md
```

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Jenkins with Maven and Pipeline plugins

## Building the Project

### Local Build

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the application (creates standalone JAR with all dependencies)
mvn package

# Run the web application
java -jar target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar
```

### Accessing the Web Application

Once the application is running, access it in your browser:

- **Home Page**: http://localhost:4567
- **Calculator API**:
  - Add: http://localhost:4567/api/add/10/5
  - Subtract: http://localhost:4567/api/subtract/20/8
  - Multiply: http://localhost:4567/api/multiply/6/7
  - Divide: http://localhost:4567/api/divide/50/5
- **Health Check**: http://localhost:4567/health

### Features

- 🎨 **Beautiful Web UI** - Interactive calculator with modern design
- 🧮 **Calculator API** - RESTful API for mathematical operations
- 📊 **Real-time Results** - Instant calculations via AJAX
- ✅ **Health Monitoring** - Built-in health check endpoint
- 🚀 **Production Ready** - Standalone JAR with all dependencies

## Jenkins Setup

### 1. Configure Jenkins Tools

Go to **Manage Jenkins** → **Global Tool Configuration**:

- **JDK**: Add JDK 11 installation (name it "JDK 11")
- **Maven**: Add Maven installation (name it "Maven 3.9.0")

### 2. Create Pipeline Job

1. Create a new **Pipeline** job in Jenkins
2. Under **Pipeline** section:
   - **Definition**: Pipeline script from SCM
   - **SCM**: Git
   - **Repository URL**: `https://github.com/atulkamble/jenkins-maven-project.git`
   - **Branch**: `*/main`
   - **Script Path**: Jenkinsfile

### 3. Run the Pipeline

Click **Build Now** to trigger the pipeline.

## Pipeline Stages

The Jenkinsfile defines the following stages:

1. **Checkout**: Pulls the latest code from the repository
2. **Build**: Compiles the source code using `mvn clean compile`
3. **Test**: Runs unit tests using `mvn test`
4. **Package**: Creates a JAR file using `mvn package`
5. **Archive**: Archives the build artifacts

## Features

- ✅ Maven-based Java project
- ✅ Jenkins declarative pipeline
- ✅ Unit tests with JUnit
- ✅ Automated build, test, and package stages
- ✅ Test result publishing
- ✅ Artifact archiving
- ✅ Post-build cleanup

## Customization

### Modify Java Version

Edit `pom.xml` to change Java version:

```xml
<maven.compiler.source>11</maven.compiler.source>
<maven.compiler.target>11</maven.compiler.target>
```

### Modify Pipeline

Edit `Jenkinsfile` to add more stages like:
- Code quality analysis (SonarQube)
- Deployment stages
- Docker image building
- Notifications (email, Slack)

## Troubleshooting

- **Maven tool not found**: Verify Maven tool name in Jenkinsfile matches Jenkins configuration
- **JDK not found**: Verify JDK tool name in Jenkinsfile matches Jenkins configuration
- **Tests failing**: Run `mvn test` locally to debug

## License

This project is for demonstration purposes.
