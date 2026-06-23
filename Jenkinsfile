pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 15, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                echo 'Query service code checkout completed.'
            }
        }

        stage('Check Maven') {
            steps {
                bat 'mvn -v'
            }
        }

        stage('List Workspace Files') {
            steps {
                bat 'dir'
            }
        }

        stage('Build Query Service') {
            steps {
                script {
                    if (fileExists('pom.xml')) {
                        bat 'mvn clean package -DskipTests'
                    } else if (fileExists('query/pom.xml')) {
                        dir('query') {
                            bat 'mvn clean package -DskipTests'
                        }
                    } else {
                        error 'pom.xml not found in root or query folder'
                    }
                }
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'Query service CI pipeline completed successfully.'
        }

        failure {
            echo 'Query service CI pipeline failed. Check console logs.'
        }
    }
}