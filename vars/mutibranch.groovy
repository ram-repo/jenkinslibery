def call() {
   
properties([
    parameters([
        choice(name: 'projectsview', description: 'Please pick one', choices: ['dev', 'qa', 'test', 'prod']),
        string(name: 'Name', description: 'Repo Name') // defaultValue: 'job-dsl-plugin'
    ])])

stages{
    stage('New MutiBranch pipline'){
        steps{
            script{
               working.createNewJenkinsJob("$params.projectsview", "$params.Name")
                }
               }
            }
        }
    }
