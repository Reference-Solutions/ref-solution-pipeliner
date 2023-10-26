import com.refSolution.pipelinerdepot.pipelines.M7Pipeline

import com.bosch.pipeliner.*

/**
 * execM7Pipeline
 * The script is used to run M7Pipeline.
 * It can be invoked from a Jenkinsfile by writing 'execM7Pipeline()'
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
    M7Pipeline pipeline = new M7Pipeline(this, environment, ioMap)
    ioMap = pipeline.run()
}
