
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
            includes("master main feature/* bugfix/* hotfix/* release/*")
            excludes("donotbuild/*")
            traits: [
                gitHubBranchDiscovery(3), 
                gitHubPullRequestDiscovery(2), 
                gitHubForkDiscovery(strategyId: 1, trust: gitHubTrustPermissions()), 
                gitHubTagDiscovery(3)
            ]
        }
        configure {
            def traits = it / sources / data / 'jenkins.branch.BranchSource' / source / traits
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
                 targets()
                 }
            traits << 'jenkins.plugins.git.traits.RefSpecsSCMSourceTrait' {
              templates {
                'jenkins.plugins.git.traits.RefSpecsSCMSourceTrait_-RefSpecTemplate' {
                  value('+refs/heads/*:refs/remotes/@{remote}/*')
                }
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
