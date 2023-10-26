import com.refSolution.pipelinerdepot.pipelines.QnxBuild

import com.bosch.pipeliner.*

/**
 * execQnxBuild
 * The script is used to run QnxBuild.
 * It can be invoked from a Jenkinsfile by writing 'execQnxBuild()'
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
    QnxBuild pipeline = new QnxBuild(this, environment, ioMap)
    ioMap = pipeline.runStages()
}
