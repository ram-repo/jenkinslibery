package com.abc
 
import javaposse.jobdsl.dsl.DslFactory
 
class GithubMultibranch {
 
    String name
    String repositoryName
    

    void build(DslFactory dslFactory) {
        def job = dslFactory.multibranchPipelineJob(name) {
            multibranchPipelineJob('devOps2') {
                branchSources {
                    github {
                        id('91179757') // IMPORTANT: use a constant and unique identifier
                        scanCredentialsId('github-ci')
                        repoOwner('ram-repo')
                        repository(repositoryName) //job-dsl-plugin
                        includes('master feature/* bugfix/* hotfix/* release/*')
                        excludes('donotbuild/*')
                    }
                }
                factory {
                    workflowBranchProjectFactory {
                        scriptPath('jenkinsFile.groovy')
                    }
                }
                triggers {
                    periodicFolderTrigger {
                        interval('2m')
                    }
                }
                orphanedItemStrategy {
                    discardOldItems {
                        numToKeep(10)
                    }
                }
            }

        }
    }
}
