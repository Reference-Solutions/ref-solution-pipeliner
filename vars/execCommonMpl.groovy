import com.refSolution.pipelinerdepot.pipelines.CommonMplPipeline


import com.bosch.pipeliner.*

/**
 * execCommonPipeline
 * The script is used to run CommonPipeline.
 * It can be invoked from a Jenkinsfile by writing 'execCommonPipeline()'
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
    CommonMplPipeline pipeline = new CommonMplPipeline(this, defaults, environment, ioMap)
    ioMap = pipeline.run()
    
}