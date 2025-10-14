pipeline {
    agent any
    
    tools {
        maven 'myMaven' // Make sure this matches your Jenkins Maven configuration
        jdk 'myJDK'        // Make sure this matches your Jenkins JDK configuration
    }
    
    environment {
        // Define environment variables
        APP_NAME = 'jenkins-maven-project'
        BUILD_VERSION = "${env.BUILD_NUMBER}"
        MAVEN_OPTS = '-Dmaven.test.failure.ignore=false'
    }
    
    options {
        // Keep only last 10 builds
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // Add timestamps to console output
        timestamps()
        // Timeout for entire pipeline
        timeout(time: 30, unit: 'MINUTES')
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
                sh 'mvn --version'
                sh 'java -version'
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
                }
                success {
                    echo '✓ All tests passed successfully!'
                }
                failure {
                    echo '✗ Tests failed!'
                }
            }
        }
        
        stage('Code Coverage') {
            steps {
                echo '================================================'
                echo 'Stage: Code Coverage'
                echo '================================================'
                echo 'Generating code coverage report...'
                sh 'mvn verify'
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
                        sha256sum target/*.jar || shasum -a 256 target/*.jar || echo "Checksum tool not available"
                    '''
                    
                    // Display deployment configuration
                    echo '\nDeployment Configuration:'
                    echo "Timestamp: ${new Date()}"
                    echo "Jenkins URL: ${env.JENKINS_URL}"
                    echo "Build URL: ${env.BUILD_URL}"
                    
                    // Simulate deployment steps
                    echo '\nSimulating Deployment Steps:'
                    echo '1. Validating artifact...'
                    sh 'test -f target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar && echo "✓ Artifact validation passed"'
                    
                    echo '2. Preparing deployment environment...'
                    sh 'echo "✓ Environment ready for deployment"'
                    
                    echo '3. Backing up previous version...'
                    sh 'echo "✓ Backup completed (simulated)"'
                    
                    echo '4. Deploying application...'
                    sh '''
                        echo "✓ Application deployed successfully (simulated)"
                        echo ""
                        echo "Deployment Summary:"
                        echo "  - Application: jenkins-maven-project"
                        echo "  - Version: 1.0-SNAPSHOT"
                        echo "  - Build: #${BUILD_NUMBER}"
                        echo "  - JAR: jenkins-maven-project-1.0-SNAPSHOT-standalone.jar"
                        echo "  - Status: Ready"
                        echo ""
                        echo "To run the web application locally:"
                        echo "  java -jar target/jenkins-maven-project-1.0-SNAPSHOT-standalone.jar"
                        echo "  Then open: http://localhost:4567"
                    '''
                    
                    echo '\n✓ Deployment completed successfully!'
                    
                    // Uncomment below for actual deployment
                    // Example: Deploy to server
                    // sh 'scp target/*-standalone.jar user@server:/deploy/path/'
                    // sh 'ssh user@server "systemctl restart app-service"'
                    
                    // Example: Deploy to Docker
                    // sh 'docker build -t myapp:${BUILD_NUMBER} .'
                    // sh 'docker push myapp:${BUILD_NUMBER}'
                    
                    // Example: Deploy to Kubernetes
                    // sh 'kubectl set image deployment/myapp myapp=myapp:${BUILD_NUMBER}'
                }
            }
            post {
                success {
                    echo '✓ Deployment stage completed successfully'
                    echo 'Web application is ready to run!'
                    echo 'Download the JAR from Jenkins artifacts and run:'
                    echo 'java -jar jenkins-maven-project-1.0-SNAPSHOT-standalone.jar'
                }
                failure {
                    echo '✗ Deployment stage failed'
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
        }
        failure {
            echo '================================================'
            echo '✗ PIPELINE FAILED!'
            echo '================================================'
            echo "Build #${env.BUILD_NUMBER} failed"
            echo "Please check the logs for details"
        }
        unstable {
            echo '================================================'
            echo '⚠ PIPELINE UNSTABLE'
            echo '================================================'
        }
        always {
            echo 'Cleaning up workspace...'
            // Comment out cleanWs() if you want to keep workspace for debugging
            // cleanWs()
        }
    }
}
