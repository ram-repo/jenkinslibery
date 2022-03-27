def call() {

properties([
    parameters([
        choice(name: 'projectsview', description: 'Please pick one', choices: ['dev', 'qa', 'test', 'prod']),
        string(name: 'repoName', description: 'RepoName ', defaultValue: 'game-of-life')
    ])])

pipeline {
    agent any
    stages{
        stage('New MutiBranch pipline'){
            steps{
                script{
                    dsl.createNewJenkinsJob("$params.projectsview","$params.repoName")
                   }
                }
            }
        }
    }
}
