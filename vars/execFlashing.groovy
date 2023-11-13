import com.refSolution.pipelinerdepot.pipelines.Flashing

import com.bosch.pipeliner.*

/**
 * execFlashing
 * The script is used to run Flashing.
 * It can be invoked from a Jenkinsfile by writing 'execFlashing()'
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
    Flashing pipeline = new Flashing(this, environment, ioMap, params.NODE_LABEL_EXPR)
    ioMap = pipeline.runStages()
}
