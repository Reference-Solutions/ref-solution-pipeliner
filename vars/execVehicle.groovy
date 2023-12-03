import com.refSolution.pipelinerdepot.pipelines.CommonVehiclepackage

import com.bosch.pipeliner.*

/**
 * execVehicle
 * The script is used to run CommonVehiclepackage.
 * It can be invoked from a Jenkinsfile by writing 'execVehicle()'
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
    CommonVehiclepackage pipeline = new CommonVehiclepackage(this, environment, ioMap)
    ioMap = pipeline.runStages()
    //ioMap = pipeline.postParallelFinally()
   
}