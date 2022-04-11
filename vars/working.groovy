import javaposse.jobdsl.dsl.DslFactory
import hudson.plugins.git.*
import hudson.*
import hudson.security.*
import java.util.*
import jenkins.plugins.git.traits.*

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
            excludes("donotbuild/*"
        }
        configure {
        // Re-write Git traits
        def traits = it / sources / data / 'jenkins.branch.BranchSource' / source / traits
        // Discover branches
        // See: https://github.com/jenkinsci/github-branch-source-plugin/blob/master/src/main/java/org/jenkinsci/plugins/github_branch_source/BranchDiscoveryTrait.java
        traits << 'org.jenkinsci.plugins.github_branch_source.BranchDiscoveryTrait' {
          // Select only branches (no PRs)
          strategyId(1)
        }
        // Discover tags
        // See: https://github.com/jenkinsci/github-branch-source-plugin/blob/master/src/main/java/org/jenkinsci/plugins/github_branch_source/TagDiscoveryTrait.java
        traits << 'org.jenkinsci.plugins.github_branch_source.TagDiscoveryTrait' { }
        // Discover pull-requests
        // See: https://github.com/jenkinsci/github-branch-source-plugin/blob/master/src/main/java/org/jenkinsci/plugins/github_branch_source/OriginPullRequestDiscoveryTrait.java
        traits << 'org.jenkinsci.plugins.github_branch_source.OriginPullRequestDiscoveryTrait' {
          // Merge automatically PRs with destination
          strategyId(1)
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
