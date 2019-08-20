pipeline {
    agent {
        docker {
            image 'maven:3.6.1-jdk-12'
            args '-v /root/.m2:/root/.m2'
        }
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn -f diner/pom.xml -B -DskipTests clean package'
            }
        }
        stage('Test') {
          steps {
              sh 'mvn test -f diner/pom.xml'
          }
      }
      stage('Sonar') {
        steps {
            sh 'mvn sonar:sonar -Dsonar.host.url= http://sonarqube:9000 -f diner/pom.xml'
        }
      }
   }
}
