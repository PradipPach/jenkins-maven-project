pipeline {
    agent any
    
    tools {
        maven '3.8.4' // Make sure this matches your Jenkins Maven configuration
        jdk 'JDK 11'        // Make sure this matches your Jenkins JDK configuration
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
                branch 'main'
            }
            steps {
                echo '================================================'
                echo 'Stage: Deploy (Placeholder)'
                echo '================================================'
                echo 'Deployment stage - Add your deployment steps here'
                // Example deployment commands:
                // sh 'scp target/*.jar user@server:/deploy/path/'
                // sh 'ssh user@server "systemctl restart app-service"'
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
