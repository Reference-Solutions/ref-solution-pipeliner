import com.refSolution.pipelinerdepot.pipelines.OtaNgPipeline

import com.bosch.pipeliner.*

/**
 * execOtaNgPipeline
 * The script is used to run OtaNgPipeline.
 * It can be invoked from a Jenkinsfile by writing 'execOtaNgPipeline()'
 *

 */
def call(Map stageOverriders=[:]) {
    Map ioMap = [:]
    Map environment = env.getEnvironment()
    Map defaults = [
        defaultInputs:'',
        exposed: [],
        parallel: []
    ]

    // Override variables from Jenkinsfile with parameter values defined in the job
    if (params && !(env.PIP_NO_PARAMS || env.NO_PARAMS)) {
        params.each { k, v ->
            environment[k] = v.toString()
        }
    }

    // Run the pipeline
    OtaNgPipeline pipeline = new OtaNgPipeline(this, defaults, environment, ioMap)
    ioMap = pipeline.run()
}
