pipeline {

    agent any

    environment {
        AWS_REGION = 'us-west-1'
        EKS_CLUSTER_NAME = 'backend-dev-cluster'
    }

    stages {

        stage('PULL') {
            steps {
                git branch: 'dev',
                    url: 'https://github.com/aditya-mohod/app-pro.git'
            }
        }

        stage('CONNECT EKS') {
            steps {
                withCredentials([
                    [$class: 'AmazonWebServicesCredentialsBinding',
                     credentialsId: 'aws-credentials']
                ]) {
                    sh '''
                        aws eks update-kubeconfig \
                            --region $AWS_REGION \
                            --name $EKS_CLUSTER_NAME
                    '''
                }
            }
        }

        stage('APPLY DATABASE') {
            steps {
                withCredentials([
                    [$class: 'AmazonWebServicesCredentialsBinding',
                     credentialsId: 'aws-credentials']
                ]) {
                    sh '''
                        
                        kubectl apply -f app/database/k8s/
                    '''
                }
            }
        }

        stage('CHECK DATABASE') {
            steps {
                withCredentials([
                    [$class: 'AmazonWebServicesCredentialsBinding',
                     credentialsId: 'aws-credentials']
                ]) {
                    sh '''
                        kubectl get pods
                        kubectl get svc
                        kubectl get pvc
                    '''
                }
            }
        }

    }
}

