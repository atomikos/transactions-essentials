pipeline {
    agent any
    tools {
        maven 'maven-3.6.0'
    }
// Use the pomfix tool to validate that bundle dependencies are properly declared
        stage('Build') {
            steps {
                retry(3) {
                    checkout scm
                }
                withMaven(maven: 'maven-3.6.0', jdk: 'jdk8-latest', mavenOpts: '-Xmx1024m -Xms512m') {
                    sh 'mvn clean install'
                }
            }
        }    
}