@Library('jenkinslib')_

import utilities.GithubMultibranch

def call() {

properties([
    parameters([
        choice(name: 'projectsview', description: 'Please pick one', choices: ['dev', 'qa', 'test', 'prod']),
        string(name: 'repoName', description: 'id github ', defaultValue: 'test')
    ])])

pipeline {
    agent any
    stages{
        stage('New MutiBranch pipline'){
            steps{
                script{
                    def multiPipeline = new GithubMultibranch("$params.projectsview", "$params.repoName").build(this)
                    }
                }
            }
        }
    }
}
