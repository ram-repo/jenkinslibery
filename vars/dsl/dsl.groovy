def createNewJenkinsJobWithMultiBranch(String  projectView, String repoName ){

    jobDsl additionalParameters: [
        projectView: projectView,
        repoName: repoName,
        scmCredsID: scmCredsID
    ], scriptText: '''
    multibranchPipelineJob("${projectView}/${repoName}") {
    branchSources {
        github {
            id('34343434') // IMPORTANT: use a constant and unique identifier
            scanCredentialsId('BREP-GitHubApp')
            repoOwner('bre-org')
            repository("${repoName}")
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

return this
