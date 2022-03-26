@Library('jenkinslib')_

import utilities.GithubMultibranch

def multiPipeline = new GithubMultibranch(
    String name,
    String repositoryName
).build(this)
