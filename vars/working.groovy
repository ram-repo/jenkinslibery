import javaposse.jobdsl.dsl.DslFactory
import hudson.plugins.git.*
import hudson.*
import jenkins.model.*
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
    multibranchPipelineJob("${projectName}/${destProject}") {
    branchSources {
        github {
            id('91179757') // IMPORTANT: use a constant and unique identifier
            scanCredentialsId('github-ci')
            repoOwner('ram-repo')
            repository("${destProject}")
            includes("master feature/* bugfix/* hotfix/* release/*")
            excludes("donotbuild/*")
            }
        }
         configure {
         def traits = it / 'sources' / 'data' / 'jenkins.branch.BranchSource' / 'source' / 'traits'
         traits << 'org.jenkinsci.plugins.github__branch__source.BranchDiscoveryTrait' {
         strategyId(3)
         }
         traits << 'org.jenkinsci.plugins.github__branch__source.OriginPullRequestDiscoveryTrait' {
         strategyId(1)
         }
         traits << 'org.jenkinsci.plugins.github__branch__source.TagDiscoveryTrait'()
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


def deletejob(String name){
    def matchedJobs = Jenkins.instance.getItemByFullName(name)
    matchedJobs.each { job ->
        println job.name
        job.delete()
    }
}
