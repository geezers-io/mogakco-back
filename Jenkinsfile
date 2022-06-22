pipeline {
    agent any

    environment {
        PASSWORD = credentials('jeidiiy-docker-hub-pw')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'fix/MK-37',
                    credentialsId: 'jenkins-github-access-token',
                    url: 'https://github.com/geezers-io/mogakco-back.git'
            }
        }

        stage('Test') {
            steps {
                sh './jenkins/test/test.sh'
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

        stage('Build') {
            steps {
                sh '''
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

        stage('Push') {
            steps {
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
            steps {
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