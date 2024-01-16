import com.refSolution.pipelinerdepot.pipelines.CommonMplPipeline

import com.bosch.pipeliner.*

/**
 * execQnxPipeline
 * The script is used to run QnxPipeline.
 * It can be invoked from a Jenkinsfile by writing 'execQnxPipeline()'
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
    CommonMplPipeline pipeline = new CommonMplPipeline(this, environment, ioMap)
    ioMap = pipeline.run()
}