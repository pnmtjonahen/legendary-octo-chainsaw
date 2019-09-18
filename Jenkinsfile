pipeline {
    agent {
        docker {
            image 'maven:3.6.1-jdk-12'
            args '-v /root/.m2:/root/.m2 --network=local-sb-network'
        }
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn -f diner/pom.xml -B -DskipTests clean package'
                sh 'mvn -f bar/pom.xml -B -DskipTests clean package'
                sh 'mvn -f kitchen/pom.xml -B -DskipTests clean package'
                sh 'mvn -f front/pom.xml -B -DskipTests clean package'
            }
        }
        stage('Test') {
          steps {
              sh 'mvn test -f diner/pom.xml'
              sh 'mvn test -f bar/pom.xml'
              sh 'mvn test -f kitchen/pom.xml'
              sh 'mvn test -f front/pom.xml'
          }
      }
      stage('Sonar') {
        steps {
            sh 'mvn sonar:sonar -Dsonar.host.url=http://sonarqube:9000 -f diner/pom.xml'
            sh 'mvn sonar:sonar -Dsonar.host.url=http://sonarqube:9000 -f bar/pom.xml'
            sh 'mvn sonar:sonar -Dsonar.host.url=http://sonarqube:9000 -f kitchen/pom.xml'
            sh 'mvn sonar:sonar -Dsonar.host.url=http://sonarqube:9000 -f front/pom.xml'
        }
      }
      stage('Dependency Check') {
        steps {
          sh 'mvn org.owasp:dependency-check-maven:check -Dformat=XML -DdataDirectory=/usr/share/nvd -f diner/pom.xml'
          sh 'mvn org.owasp:dependency-check-maven:check -Dformat=XML -DdataDirectory=/usr/share/nvd -DautoUpdate=false -f bar/pom.xml'
          sh 'mvn org.owasp:dependency-check-maven:check -Dformat=XML -DdataDirectory=/usr/share/nvd -DautoUpdate=false -f kitchen/pom.xml'
          sh 'mvn org.owasp:dependency-check-maven:check -Dformat=XML -DdataDirectory=/usr/share/nvd -DautoUpdate=false -f front/pom.xml'
          step([$class: 'DependencyCheckPublisher', unstableTotalAll: '0'])
        }
      }

   }
}
