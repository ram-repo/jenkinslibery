def call() {

properties([
    parameters([
        choice(name: 'projectsview', description: 'Please pick one', choices: ['dev', 'qa', 'test', 'prod']),
        string(name: 'repoName', description: 'id github ', defaultValue: 'job-dsl-plugin')
    ])])

pipeline {
    agent any
    stages{
        stage('New MutiBranch pipline'){
            steps{
                script{
                    GithubMultibranch.GithubMultibranch("$params.projectsview", "$params.repoName")
                    }
                }
            }
        }
    }
}
