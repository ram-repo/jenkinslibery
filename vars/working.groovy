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
                def traits = it / sources / data / 'jenkins.branch.BranchSource' / source / traits
                if (this.discoverBranches) {
                    traits << 'org.jenkinsci.plugins.github_branch_source.BranchDiscoveryTrait' {
                        strategyId(3) // detect all branches -refer the plugin source code for various options
                    }
                }
                if (this.discoverTags) {
                    traits << 'org.jenkinsci.plugins.github_branch_source.TagDiscoveryTrait' { }
                }
                if (this.includeMasterRef) {
                        traits << 'jenkins.plugins.git.traits.RefSpecsSCMSourceTrait' {
                       tempates(new RefSpecTemplate('+refs/heads/master:refs/remotes/@{remote}/master'))
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
