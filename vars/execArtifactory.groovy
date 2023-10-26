import com.refSolution.pipelinerdepot.pipelines.CommonArtifactory

import com.bosch.pipeliner.*

/**
 * execArtifactory
 * The script is used to run CommonArtifactory.
 * It can be invoked from a Jenkinsfile by writing 'execArtifactory()'
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
    CommonArtifactory pipeline = new CommonArtifactory(this, environment, ioMap)
    ioMap = pipeline.runStages()
}