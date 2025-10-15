# Jenkins Pipeline Stages - Visual Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    JENKINS MAVEN PROJECT PIPELINE v1.4                      │
└─────────────────────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────────────────────┐
│ STAGE 1: CHECKOUT                                                         │
├───────────────────────────────────────────────────────────────────────────┤
│ • Clone Git repository                                                    │
│ • Record commit ID                                                        │
│ • Track branch information                                                │
│ Status: ✅ Always runs                                                    │
└───────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌───────────────────────────────────────────────────────────────────────────┐
│ STAGE 2: BUILD INFO                                                       │
├───────────────────────────────────────────────────────────────────────────┤
│ • Display environment variables                                           │
│ • Verify Java 21 installation                                             │
│ • Verify Maven 3.9.x installation                                         │
│ • Check Docker availability                                               │
│ Status: ✅ Always runs                                                    │
└───────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌───────────────────────────────────────────────────────────────────────────┐
│ STAGE 3: DEPENDENCY CHECK ⭐ NEW                                         │
├───────────────────────────────────────────────────────────────────────────┤
│ • Generate dependency tree                                                │
│ • Check for dependency updates                                            │
│ • Identify potential vulnerabilities                                      │
│ Status: ✅ Always runs                                                    │
└───────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌───────────────────────────────────────────────────────────────────────────┐
│ STAGE 4: CLEAN                                                            │
├───────────────────────────────────────────────────────────────────────────┤
│ • Remove target/ directory                                                │
│ • Clean previous build artifacts                                          │
│ • Prepare for fresh build                                                 │
│ Status: ✅ Always runs                                                    │
└───────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌───────────────────────────────────────────────────────────────────────────┐
│ STAGE 5: COMPILE                                                          │
├───────────────────────────────────────────────────────────────────────────┤
│ • Compile Java source files                                               │
│ • Target: Java 21 bytecode                                                │
│ • Generate class files                                                    │
│ Status: ✅ Always runs | ⚠️ Fails on syntax errors                       │
└───────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌───────────────────────────────────────────────────────────────────────────┐
│ STAGE 6: TEST                                                             │
├───────────────────────────────────────────────────────────────────────────┤
│ • Run JUnit tests                                                         │
│ • Generate test reports                                                   │
│ • Publish results to Jenkins                                              │
│ • Display test summary                                                    │
│ Status: ✅ Always runs | ⚠️ Fails on test failures                       │
└───────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌───────────────────────────────────────────────────────────────────────────┐
│ STAGE 7: CODE ANALYSIS ⭐ NEW                                            │
├───────────────────────────────────────────────────────────────────────────┤
│ • Scan for TODO comments                                                  │
│ • Scan for FIXME comments                                                 │
│ • Count Java source files                                                 │
│ • Count test files                                                        │
│ Status: ✅ Always runs                                                    │
└───────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌───────────────────────────────────────────────────────────────────────────┐
│ STAGE 8: PACKAGE                                                          │
├───────────────────────────────────────────────────────────────────────────┤
│ • Create standard JAR                                                     │
│ • Create standalone fat JAR with dependencies                             │
│ • Apply Maven Shade transformations                                       │
│ Status: ✅ Always runs | ⚠️ Fails on packaging errors                    │
└───────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌───────────────────────────────────────────────────────────────────────────┐
│ STAGE 9: ARCHIVE ARTIFACTS                                                │
├───────────────────────────────────────────────────────────────────────────┤
│ • Archive JAR files to Jenkins                                            │
│ • Generate fingerprints                                                   │
│ • Display JAR contents                                                    │
│ Status: ✅ Always runs                                                    │
└───────────────────────────────────────────────────────────────────────────┘
                                    ↓
                          ┌─────────────────┐
                          │ Branch Check    │
                          │ main only?      │
                          └─────────────────┘
                                    ↓
                         ┌─────────┴─────────┐
                        YES                 NO
                         ↓                   ↓
┌───────────────────────────────────────┐   Skip Docker stages
│ STAGE 10: DOCKER BUILD ⭐ NEW        │   Continue to Deploy
├───────────────────────────────────────┤
│ • Build Docker image                  │
│ • Tag with build number               │
│ • Tag as latest                       │
│ • Display image information           │
│ Status: 🔵 Conditional (main branch)  │
└───────────────────────────────────────┘
                  ↓
┌───────────────────────────────────────┐
│ STAGE 11: SECURITY SCAN ⭐ NEW       │
├───────────────────────────────────────┤
│ • Scan Docker image with Trivy        │
│ • Report HIGH/CRITICAL vulnerabilities│
│ • Generate security report            │
│ Status: 🔵 Conditional (main branch)  │
│         ⚠️ Optional (requires Trivy)  │
└───────────────────────────────────────┘
                  ↓
┌───────────────────────────────────────────────────────────────────────────┐
│ STAGE 12: DEPLOY                                                          │
├───────────────────────────────────────────────────────────────────────────┤
│ • Validate artifacts                                                      │
│ • Perform health checks                                                   │
│ • Display deployment options:                                             │
│   - Option 1: Run with JAR                                                │
│   - Option 2: Run with Docker                                             │
│   - Option 3: Run with Docker Compose                                     │
│ • Simulate deployment (ready for production customization)                │
│ Status: 🔵 Conditional (main branch)                                      │
└───────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌───────────────────────────────────────────────────────────────────────────┐
│ POST-BUILD ACTIONS                                                        │
├───────────────────────────────────────────────────────────────────────────┤
│ ✅ SUCCESS:                                                               │
│    • Display success message                                              │
│    • Show artifact information                                            │
│    • Provide next steps                                                   │
│                                                                           │
│ ❌ FAILURE:                                                               │
│    • Display failure message                                              │
│    • Suggest troubleshooting steps                                        │
│    • List common issues                                                   │
│                                                                           │
│ ⚠️ UNSTABLE:                                                              │
│    • Warn about warnings/test failures                                    │
│    • Recommend review                                                     │
│                                                                           │
│ 🔄 ALWAYS:                                                                │
│    • Display build duration                                               │
│    • Show final result                                                    │
│    • Optional: Clean workspace                                            │
└───────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                              ENVIRONMENT VARIABLES                          │
├─────────────────────────────────────────────────────────────────────────────┤
│ APP_NAME            = 'jenkins-maven-project'                               │
│ BUILD_VERSION       = ${env.BUILD_NUMBER}                                   │
│ MAVEN_OPTS          = '-Dmaven.test.failure.ignore=false'                  │
│ DOCKER_IMAGE        = 'atulkamble/jenkins-maven-project' ⭐ NEW            │
│ DOCKER_TAG          = ${env.BUILD_NUMBER} ⭐ NEW                           │
│ PORT                = '5000' ⭐ NEW                                         │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                              PIPELINE OPTIONS                               │
├─────────────────────────────────────────────────────────────────────────────┤
│ • buildDiscarder: Keep last 10 builds                                       │
│ • timestamps: Add timestamps to console output                              │
│ • timeout: 30 minutes maximum                                               │
│ • disableConcurrentBuilds: Prevent simultaneous builds ⭐ NEW              │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                              TOOLS REQUIRED                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│ ✅ Java 21 LTS       (configured as 'myJDK')                               │
│ ✅ Maven 3.9.x       (configured as 'myMaven')                             │
│ 🔵 Docker            (optional, for Docker stages)                          │
│ 🔵 Trivy             (optional, for security scanning)                      │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                           DEPLOYMENT ARTIFACTS                              │
├─────────────────────────────────────────────────────────────────────────────┤
│ 📦 jenkins-maven-project-1.0-SNAPSHOT.jar                                   │
│    Standard JAR (requires dependencies on classpath)                        │
│                                                                             │
│ 📦 jenkins-maven-project-1.0-SNAPSHOT-standalone.jar                        │
│    Fat JAR with all dependencies (recommended)                              │
│    Size: ~10 MB                                                             │
│    Main Class: com.example.WebApp                                           │
│                                                                             │
│ 🐳 Docker Image: atulkamble/jenkins-maven-project:${BUILD_NUMBER}          │
│    Multi-stage build                                                        │
│    Base: eclipse-temurin:21-jre-alpine                                      │
│    Size: ~200 MB                                                            │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                            PERFORMANCE METRICS                              │
├─────────────────────────────────────────────────────────────────────────────┤
│ Average Build Time:   ~2-3 minutes                                          │
│ - Checkout:           ~5 seconds                                            │
│ - Dependency Check:   ~10 seconds                                           │
│ - Compile:            ~5 seconds                                            │
│ - Test:               ~3 seconds                                            │
│ - Package:            ~15 seconds                                           │
│ - Docker Build:       ~60-90 seconds (if enabled)                           │
│ - Security Scan:      ~30 seconds (if enabled)                              │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                         TROUBLESHOOTING RESOURCES                           │
├─────────────────────────────────────────────────────────────────────────────┤
│ 🔧 debug-pipeline.sh              - Automated diagnostics                   │
│ 📖 JENKINS_TROUBLESHOOTING.md     - Comprehensive guide                     │
│ 📖 PIPELINE_UPDATE_SUMMARY.md     - Update documentation                    │
│ 📖 README.md                      - Project overview                        │
│ 📖 DOCKER.md                      - Docker instructions                     │
│ 📖 RUNNING.md                     - Running guide                           │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                              LEGEND                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│ ✅  Always runs                                                             │
│ 🔵  Conditional (depends on branch or configuration)                        │
│ ⚠️  May fail or produce warnings                                            │
│ ⭐  New feature in v1.4                                                     │
│ 📦  Artifact output                                                         │
│ 🐳  Docker related                                                          │
│ 🔧  Tool or script                                                          │
│ 📖  Documentation                                                           │
└─────────────────────────────────────────────────────────────────────────────┘

```

## Quick Start

### 1. Configure Jenkins
```bash
# Set up tools in Jenkins Global Tool Configuration
- JDK: myJDK (Java 21)
- Maven: myMaven (3.9.x)
```

### 2. Create Pipeline Job
```bash
# In Jenkins:
New Item → Pipeline → Configure
SCM: Git
Repository: https://github.com/atulkamble/jenkins-maven-project
Branch: main
Script Path: Jenkinsfile
```

### 3. Run Build
```bash
# Triggers automatically on commit or manually
Build Now → View Console Output
```

### 4. Access Artifacts
```bash
# Download from Jenkins build page
Build #X → Artifacts → jenkins-maven-project-1.0-SNAPSHOT-standalone.jar
```

### 5. Deploy
```bash
# Option 1: Direct JAR execution
java -jar jenkins-maven-project-1.0-SNAPSHOT-standalone.jar

# Option 2: Docker
docker run -d -p 5000:5000 atulkamble/jenkins-maven-project:latest

# Option 3: Docker Compose
docker-compose up -d
```

## Success Criteria

| Metric | Target | Actual |
|--------|--------|--------|
| Build Time | < 5 min | ~1.5s (local), ~2-3min (full) |
| Test Pass Rate | 100% | ✅ 100% (5/5) |
| Code Coverage | > 60% | 📊 Check reports |
| Vulnerabilities | < 5 HIGH | ✅ 1 HIGH |
| Deployment Options | > 1 | ✅ 3 options |

---

**Pipeline Version:** 1.4  
**Last Updated:** October 15, 2025  
**Status:** ✅ Production Ready
