import com.refSolution.pipelinerdepot.pipelines.CommonNotification

import com.bosch.pipeliner.*

/**
 * execNotification
 * The script is used to run CommonNotification.
 * It can be invoked from a Jenkinsfile by writing 'execNotification()'
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
    CommonNotification pipeline = new CommonNotification(this, environment, ioMap)
    ioMap = pipeline.runStages()
    //ioMap = pipeline.postParallelFinally()
   
}