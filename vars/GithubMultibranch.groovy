package mutibranch

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.*

/* groovylint-disable-next-line ClassNameSameAsFilename */
class MultibranchPipelineJobBuilder {

    String projectView
    String repoName

    MultibranchWorkflowJob build(DslFactory factory) {
        factory.multibranchPipelineJob(projectView) {
            branchSources {
                github {
                    id('34343434') // IMPORTANT: use a constant and unique identifier
                    scanCredentialsId('BREP-GitHubApp')
                    repoOwner('bre-org')
                    repository(repoName)
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
