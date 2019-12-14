pipeline {
    agent any
    tools{
             maven 'Maven 3.6.2'
         }

    stages {
        stage('build') {
             steps {
                git branch: 'restAssured_TodoListController',
                credentialsId: 'a60f361f-7560-4eb2-af7f-3d5189fd1f4b',
                url: 'git@github.com:hejhj/JavaToDoApi.git'

                sh "mvn -DskipTests=true clean package"
                sh "./runApp.sh"
                sh "sleep 3"
             }


      }

        stage('test'){
                steps{
                    sh "mvn test"
                    sh "kill -9 \$(lsof -t -i:8081) || echo \"Process was not running.\""
            }

                post {
                    success {
                       junit '**/target/surefire-reports/TEST-TestSuite.xml'
                       archiveArtifacts 'target/*.jar'
                    }
         }
      }
   }
}