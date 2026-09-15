pipeline {

    agent any

    environment {
        AWS_REGION = 'us-west-1'
        EKS_CLUSTER_NAME = 'backend-dev-cluster'
        ECR_REGISTRY = '725166342186.dkr.ecr.us-west-1.amazonaws.com/easy-backend'
        IMAGE_NAME = 'easy-backend'
    }

    stages {

        stage('PULL') {
            steps {
                git branch: 'dev',
                    url: 'https://github.com/aditya-mohod/app-pro.git'
            }
        }

        // stage('BUILD') {
        //     steps {
        //         sh '''
        //             cd app/backend/
        //              clean package -DskipTests
        //         '''
        //     }
        // }

        // stage('DOCKER BUILD') {
        //     steps {
        //         sh '''
        //             cd app/backend/

        //             docker build \
        //                 -t $ECR_REGISTRY/$IMAGE_NAME:$BUILD_NUMBER \
        //                 -t $ECR_REGISTRY/$IMAGE_NAME:latest .
        //         '''
        //     }
        // }

        stage ('FRONTEND-BUILD-DOCKERFILE') {
            steps {
                sh '''cd app/backend/
                    docker build -t 725166342186.dkr.ecr.us-west-1.amazonaws.com/easy-backend .'''
            }
        }

        stage('PUSH') {
            steps {
                withCredentials([
                    [$class: 'AmazonWebServicesCredentialsBinding',
                     credentialsId: 'aws-credentials']
                ]) {
                    sh '''
                        

            
                        aws ecr get-login-password --region us-west-1 | 
                        docker login --username AWS --password-stdin 725166342186.dkr.ecr.eu-north-1.amazonaws.com

                        docker push 725166342186.dkr.ecr.eu-north-1.amazonaws.com/easy-backend
                    '''
                }
            }
        }

        // stage('APPLY') {
        //     steps {
        //         withCredentials([
        //             [$class: 'AmazonWebServicesCredentialsBinding',
        //              credentialsId: 'aws-credentials']
        //         ]) {
        //             sh '''
        //                 aws eks update-kubeconfig \
        //                     --region $AWS_REGION \
        //                     --name $EKS_CLUSTER_NAME

        //                 kubectl create namespace cloudblitz \
        //                     --dry-run=client -o yaml | kubectl apply -f -

        //                 sed -i "s|image: cloudblitz/auth-service:latest|image: $ECR_REGISTRY/$IMAGE_NAME:$BUILD_NUMBER|g" \
        //                     app/backend/auth-service/k8s/deployment.yaml

        //                 kubectl apply -f app/backend/auth-service/k8s/
        //             '''
        //         }
        //     }
        // }

        stage('APPLY') {
            steps {
                withCredentials([
                    [$class: 'AmazonWebServicesCredentialsBinding',
                     credentialsId: 'aws-credentials']
                ]) {
                    sh '''
                        aws eks update-kubeconfig \
                            --region $AWS_REGION \
                            --name $EKS_CLUSTER_NAME

                        kubectl apply -f app/backend/k8s/
                    '''
                }
            }
        }
    }
}
