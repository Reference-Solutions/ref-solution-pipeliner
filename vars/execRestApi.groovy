import com.refSolution.pipelinerdepot.pipelines.CommonRestApi

import com.bosch.pipeliner.*

/**
 * execDac
 * The script is used to run CommonDac.
 * It can be invoked from a Jenkinsfile by writing 'execDac()'
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
    CommonRestApi pipeline = new CommonRestApi(this, environment, ioMap)
    ioMap = pipeline.runStages()
}