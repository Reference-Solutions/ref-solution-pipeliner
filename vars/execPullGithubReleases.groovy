import com.refSolution.pipelinerdepot.pipelines.PullGithubReleases

import com.bosch.pipeliner.*

/**
 * execPullGithubReleases
 * The script is used to run PullGithubReleases.
 * It can be invoked from a Jenkinsfile by writing 'execPullGithubReleases()'
 *

 */
def call(Map stageOverriders=[:]) {
    Map ioMap = [:]
    Map environment = env.getEnvironment()

    // Override variables from Jenkinsfile with parameter values defined in the job
    if (params && !(env.PIP_NO_PARAMS || env.NO_PARAMS)) {
        params.each { k, v ->
            environment[k] = v.toString()
        }
    }

    // Run the pipeline
    PullGithubReleases pipeline = new PullGithubReleases(this, environment, ioMap)
    ioMap = pipeline.runStages()
}
