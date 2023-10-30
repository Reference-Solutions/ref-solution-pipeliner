import com.refSolution.pipelinerdepot.pipelines.CommonVerification

import com.bosch.pipeliner.*

/**
 * execCommonVerification
 * The script is used to run CommonVerification.
 * It can be invoked from a Jenkinsfile by writing 'execCommonVerification()'
 *

 */
def call(Map stageOverriders=[:]) {
    Map ioMap = [:]
    Map environment = env.getEnvironment()

    // Override variables from Jenkinsfile with parameter values defined in the job
    if (params) {
        params.each { k, v ->
            environment[k] = v.toString()
        }
    }

    // Run the pipeline
    CommonVerification pipeline = new CommonVerification(this, environment, ioMap)
    ioMap = pipeline.runStages()
}
