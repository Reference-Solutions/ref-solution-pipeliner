import com.refSolution.pipelinerdepot.pipelines.SwFactoryPipeline

import com.bosch.pipeliner.*

/**
 * execSwFactoryPipeline
 * The script is used to run SwFactoryPipeline.
 * It can be invoked from a Jenkinsfile by writing 'execSwFactoryPipeline()'
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
    SwFactoryPipeline pipeline = new SwFactoryPipeline(this, environment, ioMap)
    ioMap = pipeline.run()
}
