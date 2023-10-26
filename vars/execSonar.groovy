import com.refSolution.pipelinerdepot.pipelines.CommonSonar

import com.bosch.pipeliner.*

/**
 * execCommonSonar
 * The script is used to run CommonSonar.
 * It can be invoked from a Jenkinsfile by writing 'execCommonSonar()'
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
    CommonSonar pipeline = new CommonSonar(this, environment, ioMap)
    ioMap = pipeline.runStages()
}
