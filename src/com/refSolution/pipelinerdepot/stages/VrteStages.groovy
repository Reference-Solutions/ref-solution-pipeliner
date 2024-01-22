
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.refSolution.pipelinerdepot.utils.Jfrog

/**
* Contains stages that can be reused across pipelines
*/
class VrteStages {

    /**
     * for shallow clone using gitlab_branch_source plugin, the minimum depth
     * has to be at least the number of commits in the MR + 1, so the latest
     * commit from main branch is also fetched, and the MR can be merged into it.
     * in case the value is not sets, perform a full clone
     */
    final int defaultShallowCloneDepth = -1

    private def script
    private Map env
    private LoggerDynamic logger
    private Jfrog jfrog

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    VrteStages(script, Map env) {
        this.script = script
        this.env = env
        this.jfrog = new Jfrog(script, env)
        this.logger = new LoggerDynamic(script)
    }
    
    def stagePullArtifact(Map env, Map stageInput = [:]){
        script.stage("Vrte Pull Artifacts") {
            Map config = [:]
            config["artifactoryServerId"] = stageInput.vrte_artifactory_server_id?.trim() ?: 'artifactory-boschdevcloud'
            config["pattern"] = stageInput.vrte_artifact_pattern.trim()
            config["target"] = stageInput.vrte_artifact_download_path.trim()
            jfrog.downloadFromArtifactory(config)
        }
    }
}
