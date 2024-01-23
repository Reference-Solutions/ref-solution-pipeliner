import com.refSolution.pipelinerdepot.pipelines.HilValidation

import com.bosch.pipeliner.*

/**
 * execHilValidation
 * The script is used to run HilValidation.
 * It can be invoked from a Jenkinsfile by writing 'execHilValidation()'
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
    HilValidation pipeline = new HilValidation(this, environment, ioMap)
    ioMap = pipeline.runStages()
}
