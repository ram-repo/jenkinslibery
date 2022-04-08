
import javaposse.jobdsl.dsl.DslFactory
import hudson.plugins.git.*
import hudson.*
import hudson.security.*
import java.util.*
    
def createNewJenkinsJob(String projectName, String destProject) {
    jobDsl additionalParameters: [
       // projectsFolder: projectsFolder,
        projectName: projectName,
        destProject: destProject,
        //destGit: destGit,
        //gitUserUri: gitUser.replace('@', '%40'),
        //gitServerHost: gitServerHost,
        //scmCredsID: scmCredsID
    ], scriptText: '''
    multibranchPipelineJob("${projectName}") {
    branchSources {
        github {
            id('91179757') // IMPORTANT: use a constant and unique identifier
            scanCredentialsId('github-ci')
            repoOwner('ram-repo')
            repository("${destProject}")
            includes("master feature/* bugfix/* hotfix/* release/*")
            excludes("donotbuild/*")
        }
         configure {
         def traits = it / 'sources' / 'data' / 'jenkins.branch.BranchSource' / 'source' / 'traits'
         traits << 'org.jenkinsci.plugins.github__branch__source.ForkPullRequestDiscoveryTrait' {
         strategyId(1)
         }
         traits << 'org.jenkinsci.plugins.github__branch__source.BranchDiscoveryTrait' {
         strategyId(1)
         }
         traits << 'org.jenkinsci.plugins.github__branch__source.OriginPullRequestDiscoveryTrait' {
         strategyId(1)
         }
         traits << 'org.jenkinsci.plugins.github__branch__source.TagDiscoveryTrait' {
         strategyId(1)
         }
       }
    }
    factory {
        workflowBranchProjectFactory {
            scriptPath("jenkinsFile.groovy")
        }
      }
    triggers {
        periodicFolderTrigger {
            interval("2m")
        }
    }
    orphanedItemStrategy {
        discardOldItems {
            numToKeep(10)
        }
    }
}
    '''
}




@NonCPS
def setDescription() {
    def item = Jenkins.instance.getItemByFullName(env.JOB_NAME)
    item.setDescription('<!DOCTYPE html><html><body><p style="font-weight: bold; color: blue">==================================[Pipeline Description]=================================</p>This pipeline Creates & Delete Github repository:<ul><li>Creates MultiBranch pipeline in jenkins</li></ul><p style="font-weight: bold; color: blue">==================================[End of Description]===================================</p></body></html>')
    item.save()
}
setDescription()


properties([
    parameters([
        choice(name: 'projectsview', description: 'Please pick one', choices: ['dev', 'qaa', 'test', 'prod']),
        string(name: 'Name', description: 'RepoName ', defaultValue: 'game-of-life')
    ])
])

pipeline {
    agent any
    stages {
        stage('New MutiBranch pipline') {
            steps {
                script {
                
                createNewJenkinsJob.createNewJenkinsJob("$params.projectsview", "$params.Name")
                }
            }
        }
    }
}
