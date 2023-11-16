import com.refSolution.pipelinerdepot.pipelines.CommonQemu

import com.bosch.pipeliner.*

/**
 * execQemu
 * The script is used to run Qemu.
 * It can be invoked from a Jenkinsfile by writing 'execQemu()'
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
    CommonQemu pipeline = new CommonQemu(this, environment, ioMap)
    ioMap = pipeline.runStages()
}
