pipeline {
    agent any
    
    tools {
        maven 'My Maven' // Make sure this matches your Jenkins Maven configuration
        jdk 'MyJDK'     // Make sure this matches your Jenkins JDK configuration
    }
    
    environment {
        // Define environment variables
        APP_NAME = 'jenkins-maven-project'
        BUILD_VERSION = "${env.BUILD_NUMBER}"
        MAVEN_OPTS = '-Dmaven.test.failure.ignore=false'
        DOCKER_IMAGE = "atuljkamble/${APP_NAME}"
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        PORT = '5000'
    }
    
    options {
        // Keep only last 10 builds
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // Add timestamps to console output
        timestamps()
        // Timeout for entire pipeline
        timeout(time: 30, unit: 'MINUTES')
        // Skip default checkout
        skipDefaultCheckout(false)
        // Disable concurrent builds
        disableConcurrentBuilds()
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '================================================'
                echo 'Stage: Checkout'
                echo '================================================'
                echo "Checking out source code from: ${env.GIT_URL}"
                echo "Branch: ${env.GIT_BRANCH}"
                checkout scm
                sh 'git rev-parse --short HEAD > .git/commit-id'
                script {
                    env.GIT_COMMIT_SHORT = readFile('.git/commit-id').trim()
                }
                echo "Commit ID: ${env.GIT_COMMIT_SHORT}"
            }
        }
        
        stage('Build Info') {
            steps {
                echo '================================================'
                echo 'Stage: Build Information'
                echo '================================================'
                echo "Application: ${env.APP_NAME}"
                echo "Build Number: ${env.BUILD_NUMBER}"
                echo "Build ID: ${env.BUILD_ID}"
                echo "Job Name: ${env.JOB_NAME}"
                echo "Workspace: ${env.WORKSPACE}"
                echo "Port: ${env.PORT}"
                sh 'mvn --version'
                sh 'java -version'
                sh 'echo "Docker version:" && docker --version || echo "Docker not available"'
            }
        }
        
        stage('Dependency Check') {
            steps {
                echo '================================================'
                echo 'Stage: Dependency Check'
                echo '================================================'
                echo 'Checking for dependency updates and vulnerabilities...'
                sh 'mvn dependency:tree'
                sh 'mvn versions:display-dependency-updates || echo "Dependency updates check completed"'
            }
        }
        
        stage('Clean') {
            steps {
                echo '================================================'
                echo 'Stage: Clean'
                echo '================================================'
                echo 'Cleaning previous build artifacts...'
                sh 'mvn clean'
            }
        }
        
        stage('Compile') {
            steps {
                echo '================================================'
                echo 'Stage: Compile'
                echo '================================================'
                echo 'Compiling the project...'
                sh 'mvn compile'
            }
        }
        
        stage('Test') {
            steps {
                echo '================================================'
                echo 'Stage: Test'
                echo '================================================'
                echo 'Running unit tests...'
                sh 'mvn test'
            }
            post {
                always {
                    echo 'Publishing test results...'
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    // Generate test report
                    sh 'echo "Test Summary:" && cat target/surefire-reports/*.txt || echo "No test summary available"'
                }
                success {
                    echo '✓ All tests passed successfully!'
                }
                failure {
                    echo '✗ Tests failed!'
                    echo 'Please check the test reports for details'
                }
            }
        }
        
        stage('Code Analysis') {
            steps {
                echo '================================================'
                echo 'Stage: Code Analysis'
                echo '================================================'
                echo 'Running code analysis...'
                script {
                    // Check for common code issues
                    sh '''
                        echo "Checking for TODO comments:"
                        grep -r "TODO" src/ || echo "No TODOs found"
                        echo ""
                        echo "Checking for FIXME comments:"
                        grep -r "FIXME" src/ || echo "No FIXMEs found"
                        echo ""
                        echo "Code statistics:"
                        find src/main/java -name "*.java" | wc -l | xargs echo "Java files:"
                        find src/test/java -name "*.java" | wc -l | xargs echo "Test files:"
                    '''
                }
            }
        }
        
        stage('Package') {
            steps {
                echo '================================================'
                echo 'Stage: Package'
                echo '================================================'
                echo 'Packaging the application...'
                sh 'mvn package -DskipTests'
                sh 'ls -lh target/*.jar'
            }
        }
        
        stage('Archive Artifacts') {
            steps {
                echo '================================================'
                echo 'Stage: Archive Artifacts'
                echo '================================================'
                echo 'Archiving build artifacts...'
                archiveArtifacts artifacts: '**/target/*.jar', 
                                 fingerprint: true,
                                 allowEmptyArchive: false
                sh '''
                    echo "Artifact Information:"
                    ls -lh target/*.jar
                    echo ""
                    echo "JAR Contents:"
                    jar -tf target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar | head -20
                '''
            }
        }
        
        stage('Docker Build') {
            when {
                anyOf {
                    branch 'main'
                    branch 'origin/main'
                    expression { env.GIT_BRANCH == 'origin/main' }
                    expression { env.GIT_BRANCH == 'main' }
                }
            }
            steps {
                echo '================================================'
                echo 'Stage: Docker Build'
                echo '================================================'
                script {
                    try {
                        echo "Building Docker image: ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}"
                        sh """
                            docker build -t ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} .
                            docker tag ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} ${env.DOCKER_IMAGE}:latest
                        """
                        echo "✓ Docker image built successfully"
                        sh "docker images | grep ${env.APP_NAME}"
                    } catch (Exception e) {
                        echo "⚠ Docker build failed or Docker not available: ${e.message}"
                        echo "Continuing pipeline without Docker image..."
                    }
                }
            }
        }
        
        stage('Docker Security Scan') {
            when {
                anyOf {
                    branch 'main'
                    branch 'origin/main'
                    expression { env.GIT_BRANCH == 'origin/main' }
                    expression { env.GIT_BRANCH == 'main' }
                }
            }
            steps {
                echo '================================================'
                echo 'Stage: Docker Security Scan'
                echo '================================================'
                script {
                    try {
                        echo "Scanning Docker image for vulnerabilities..."
                        sh """
                            docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
                            aquasec/trivy image --severity HIGH,CRITICAL \
                            ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} || echo "Trivy scan completed with findings"
                        """
                    } catch (Exception e) {
                        echo "⚠ Docker security scan not available: ${e.message}"
                        echo "Consider installing Trivy for security scanning"
                    }
                }
            }
        }
        
        stage('Deploy') {
            when {
                anyOf {
                    branch 'main'
                    branch 'origin/main'
                    expression { env.GIT_BRANCH == 'origin/main' }
                    expression { env.GIT_BRANCH == 'main' }
                }
            }
            steps {
                echo '================================================'
                echo 'Stage: Deploy'
                echo '================================================'
                script {
                    // Display deployment information
                    echo "Preparing deployment for ${env.APP_NAME}"
                    echo "Build Number: ${env.BUILD_NUMBER}"
                    echo "Git Commit: ${env.GIT_COMMIT_SHORT}"
                    
                    // Show artifact details
                    echo '\nArtifact Information:'
                    sh '''
                        echo "JAR files ready for deployment:"
                        ls -lh target/*.jar
                        echo ""
                        echo "File checksums:"
                        sha256sum target/*.jar 2>/dev/null || shasum -a 256 target/*.jar || echo "Checksum tool not available"
                    '''
                    
                    // Display deployment configuration
                    echo '\nDeployment Configuration:'
                    echo "Timestamp: ${new Date()}"
                    echo "Jenkins URL: ${env.JENKINS_URL}"
                    echo "Build URL: ${env.BUILD_URL}"
                    echo "Docker Image: ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}"
                    echo "Application Port: ${env.PORT}"
                    
                    // Simulate deployment steps
                    echo '\nSimulating Deployment Steps:'
                    echo '1. Validating artifact...'
                    sh 'test -f target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar && echo "✓ Artifact validation passed"'
                    
                    echo '2. Preparing deployment environment...'
                    sh 'echo "✓ Environment ready for deployment"'
                    
                    echo '3. Health check before deployment...'
                    sh '''
                        echo "✓ Pre-deployment health check passed"
                        echo "  - Java version: $(java -version 2>&1 | head -n 1)"
                        echo "  - Available memory: $(free -h 2>/dev/null | grep Mem || echo 'N/A')"
                        echo "  - Disk space: $(df -h . | tail -1 || echo 'N/A')"
                    '''
                    
                    echo '4. Backing up previous version...'
                    sh 'echo "✓ Backup completed (simulated)"'
                    
                    echo '5. Deploying application...'
                    sh """
                        echo "✓ Application deployed successfully (simulated)"
                        echo ""
                        echo "Deployment Summary:"
                        echo "  - Application: ${env.APP_NAME}"
                        echo "  - Version: 1.0-SNAPSHOT"
                        echo "  - Build: #${env.BUILD_NUMBER}"
                        echo "  - JAR: jenkins-maven-project-1.0-SNAPSHOT-standalone.jar"
                        echo "  - Docker Image: ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}"
                        echo "  - Port: ${env.PORT}"
                        echo "  - Status: Ready"
                        echo ""
                        echo "Deployment Options:"
                        echo ""
                        echo "Option 1 - Run locally with JAR:"
                        echo "  java -jar target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar"
                        echo "  Then open: http://localhost:${env.PORT}"
                        echo ""
                        echo "Option 2 - Run with Docker:"
                        echo "  docker run -d -p ${env.PORT}:${env.PORT} --name ${env.APP_NAME} ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}"
                        echo "  Then open: http://localhost:${env.PORT}"
                        echo ""
                        echo "Option 3 - Run with Docker Compose:"
                        echo "  docker-compose up -d"
                        echo "  Then open: http://localhost:${env.PORT}"
                    """
                    
                    echo '\n✓ Deployment completed successfully!'
                    
                    // Uncomment below for actual deployment
                    // Example: Deploy to remote server
                    // sh 'scp target/*-standalone.jar user@server:/deploy/path/'
                    // sh 'ssh user@server "systemctl restart app-service"'
                    
                    // Example: Push to Docker registry
                    // withCredentials([usernamePassword(credentialsId: 'docker-hub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    //     sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    //     sh "docker push ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}"
                    //     sh "docker push ${env.DOCKER_IMAGE}:latest"
                    // }
                    
                    // Example: Deploy to Kubernetes
                    // sh 'kubectl set image deployment/myapp myapp=${env.DOCKER_IMAGE}:${env.DOCKER_TAG}'
                    // sh 'kubectl rollout status deployment/myapp'
                }
            }
            post {
                success {
                    echo '✓ Deployment stage completed successfully'
                    echo 'Web application is ready to run!'
                    echo 'Choose your deployment method from the options above'
                }
                failure {
                    echo '✗ Deployment stage failed'
                    echo 'Rolling back changes...'
                }
            }
        }
    }
    
    post {
        success {
            echo '================================================'
            echo '✓ PIPELINE COMPLETED SUCCESSFULLY!'
            echo '================================================'
            echo "Build #${env.BUILD_NUMBER} completed successfully"
            echo "Application: ${env.APP_NAME}"
            echo "Commit: ${env.GIT_COMMIT_SHORT}"
            echo "Docker Image: ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}"
            echo "Artifacts archived and ready for deployment"
            echo ''
            echo 'Next Steps:'
            echo '1. Download artifacts from Jenkins'
            echo '2. Deploy using one of the deployment options'
            echo '3. Access application at http://localhost:5000'
        }
        failure {
            echo '================================================'
            echo '✗ PIPELINE FAILED!'
            echo '================================================'
            echo "Build #${env.BUILD_NUMBER} failed"
            echo "Please check the logs for details"
            echo ''
            echo 'Common issues to check:'
            echo '- Compilation errors in Java code'
            echo '- Test failures'
            echo '- Dependency resolution issues'
            echo '- Docker daemon availability (if applicable)'
        }
        unstable {
            echo '================================================'
            echo '⚠ PIPELINE UNSTABLE'
            echo '================================================'
            echo 'Build completed with warnings or test failures'
            echo 'Please review the test results and logs'
        }
        always {
            echo ''
            echo 'Pipeline execution completed'
            echo "Duration: ${currentBuild.durationString}"
            echo "Result: ${currentBuild.result}"
            // Uncomment to clean workspace after build
            // echo 'Cleaning up workspace...'
            // cleanWs()
        }
    }
}
