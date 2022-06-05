pipeline {
    agent any

    environment {
        PASSWORD = credentials('jeidiiy-docker-hub-pw')
    }

    stages {
        stage('Build') {
            step {
                sh '''
                    ./jenkins/build/gradle.sh ./gradlew clean build -x test
                    ./jenkins/build/build.sh
                    '''
            }
            post {
                success {
                    echo '**** build success ****'
                }
                failure {
                    echo '**** build failed ****'
                }
            }
        }

        stage('Test') {
            step {
                sh './jenkins/test/gradle.sh ./gradlew test'
            }
            post {
                success {
                    echo '**** Test success ****'
                }
                failure {
                    echo '**** Test failed ****'
                }
            }
        }

        stage('Push') {
            step {
                sh './jenkins/push/push.sh'
            }
            post {
                success {
                    echo '**** Push success ****'
                }
                failure {
                    echo '**** Push failed ****'
                }
            }
        }
        stage('Deploy') {
            step {
                sh './jenkins/deploy/deploy.sh'
            }
            post {
                success {
                    echo '**** Deploy success ****'
                }
                failure {
                    echo '**** Deploy failed ****'
                }
            }
        }
    }
}