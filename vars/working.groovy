
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
        }
         configure {
         def traits = it / 'sources' / 'data' / 'jenkins.branch.BranchSource' / 'source' / 'traits'
         traits << 'org.jenkinsci.plugins.github__branch__source.BranchDiscoveryTrait' {
         strategyId(1)
         }
         traits << 'org.jenkinsci.plugins.github__branch__source.OriginPullRequestDiscoveryTrait' {
         strategyId(1)
         }
         traits << 'org.jenkinsci.plugins.github__branch__source.ForkPullRequestDiscoveryTrait' {
         strategyId(1)
         }
         // Discover tags
        // See: https://github.com/jenkinsci/github-branch-source-plugin/blob/master/src/main/java/org/jenkinsci/plugins/github_branch_source/TagDiscoveryTrait.java
        traits << 'org.jenkinsci.plugins.github_branch_source.TagDiscoveryTrait' { }
       }
        traits << 'jenkins.plugins.git.traits.GitLFSPullTrait' {}
        traits << 'jenkins.plugins.git.traits.CheckoutOptionTrait' {
            extension( class: 'hudson.plugins.git.extensions.impl.CheckoutOption' ) {
            timeout( 60 )
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
