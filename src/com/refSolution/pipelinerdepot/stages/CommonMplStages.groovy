
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.bosch.pipeliner.ScriptUtils

/**
* Contains stages that can be reused across pipelines
*/
class CommonMplStages {

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
    private ScriptUtils utils

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    CommonMplStages(script, Map env) {
        this.script = script
        this.env = env
        this.utils = new ScriptUtils(script, env)
        this.logger = new LoggerDynamic(script)
    }
    
     
        def stageBuild(Map env, Map stageInput = [:]){
        script.stage("Build") {
             logger.info("Build")
 
    }

}


}


