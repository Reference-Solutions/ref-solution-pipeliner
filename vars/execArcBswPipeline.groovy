import com.refSolution.pipelinerdepot.pipelines.ArcBswPipeline

import com.bosch.pipeliner.*

/**
 * execArcBswPipeline
 * The script is used to run ArcBswPipeline.
 * It can be invoked from a Jenkinsfile by writing 'execArcBswPipeline()'
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
    ArcBswPipeline pipeline = new ArcBswPipeline(this, environment, ioMap)
    ioMap = pipeline.run()
}
