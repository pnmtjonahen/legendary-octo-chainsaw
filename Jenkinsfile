pipeline {
    agent {
        docker {
            image 'maven:azulzulu-11'
            args '-v /root/.m2:/root/.m2'
        }
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn -f diner/pom.xml -B -DskipTests clean package'
                sh 'mvn -f bar/pom.xml -B -DskipTests clean package'
                sh 'mvn -f kitchen/pom.xml -B -DskipTests clean package'
            }
        }
        stage('Test') {
          steps {
              sh 'mvn test -f diner/pom.xml'
              sh 'mvn test -f bar/pom.xml'
              sh 'mvn test -f kitchen/pom.xml'
          }
      }
      stage('Sonar') {
        steps {
            sh 'mvn sonar:sonar -Dsonar.host.url=http://sonarqube:9000 -f diner/pom.xml'
            sh 'mvn sonar:sonar -Dsonar.host.url=http://sonarqube:9000 -f bar/pom.xml'
            sh 'mvn sonar:sonar -Dsonar.host.url=http://sonarqube:9000 -f kitchen/pom.xml'
        }
      }
   }
}
