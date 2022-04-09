package com.org

import hudson.plugins.git.*
import hudson.*
import hudson.security.*
import java.util.*

def createNewJenkinsJob(String projectsFolder, String projectName, String destProject, String destGit) {
    jobDsl additionalParameters: [
        projectsFolder: projectsFolder,
        projectName: projectName,
        destProject: destProject,
        destGit: destGit,
        gitUserUri: gitUser.replace('@', '%40'),
        gitServerHost: gitServerHost,
        scmCredsID: scmCredsID
    ], scriptText: '''
    multibranchPipelineJob('devOps2') {
    branchSources {
        github {
            id('91179757') // IMPORTANT: use a constant and unique identifier
            scanCredentialsId('github-ci')
            repoOwner('ram-repo')
            repository('job-dsl-plugin')
            includes("master feature/* bugfix/* hotfix/* release/*")
            excludes("donotbuild/*")
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
