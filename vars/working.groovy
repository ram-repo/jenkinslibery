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
            traits {
                gitHubBranchDiscovery {
                    strategyId(1)
                }
                gitHubPullRequestDiscovery {
                    strategyId(3) // 3 = both PR head and PR merge from origin
                }
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
