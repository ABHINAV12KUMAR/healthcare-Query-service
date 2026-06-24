```groovy
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

        stage('SonarQube Analysis') {
            steps {
                script {
                    if (fileExists('pom.xml')) {
                        withSonarQubeEnv('SonarQube') {
                            bat '''
                                mvn sonar:sonar ^
                                -Dsonar.projectKey=healthcare-query-service ^
                                -Dsonar.projectName=healthcare-query-service ^
                                -Dsonar.host.url=%SONAR_HOST_URL% ^
                                -Dsonar.login=%SONAR_AUTH_TOKEN%
                            '''
                        }
                    } else if (fileExists('query/pom.xml')) {
                        dir('query') {
                            withSonarQubeEnv('SonarQube') {
                                bat '''
                                    mvn sonar:sonar ^
                                    -Dsonar.projectKey=healthcare-query-service ^
                                    -Dsonar.projectName=healthcare-query-service ^
                                    -Dsonar.host.url=%SONAR_HOST_URL% ^
                                    -Dsonar.login=%SONAR_AUTH_TOKEN%
                                '''
                            }
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
            echo 'Query service CI + SonarQube pipeline completed successfully.'
        }

        failure {
            echo 'Query service CI + SonarQube pipeline failed. Check console logs.'
        }
    }
}
```
