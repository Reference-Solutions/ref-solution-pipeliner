import com.refSolution.pipelinerdepot.pipelines.CommonGit

import com.bosch.pipeliner.*

/**
 * execCommonGit
 * The script is used to run CommonGit.
 * It can be invoked from a Jenkinsfile by writing 'execCommonGit()'
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
    CommonGit pipeline = new CommonGit(this, environment, ioMap)
    ioMap = pipeline.runStages()
}
