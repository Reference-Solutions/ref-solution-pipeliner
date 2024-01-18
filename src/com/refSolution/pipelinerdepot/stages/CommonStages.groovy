
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.bosch.pipeliner.ScriptUtils

/**
* Contains stages that can be reused across pipelines
*/
class CommonStages {

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
    CommonStages(script, Map env) {
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

      def makeBuild(Map env, Map stageInput = [:]){
        String qnxSdkPath = stageInput.qnx_sdk_path?.trim() ?: 'C:/Users/zrd2kor/qnx710'
        String scm_checkout_dir = stageInput.custom_scm_checkout_dir?.trim() ?: ''
        script.bat """
            echo 'Set QNX env variable'
            call ${qnxSdkPath}/qnxsdp-env.bat
            echo 'starting building'
            cd ${scm_checkout_dir}
            make all
        """

      }

      def copyPFE(Map env, Map stageInput = [:]) {
         String scm_checkout_dir = stageInput.custom_scm_checkout_dir?.trim() ?: 'C:/Users/zrd2kor/qnx710'
        script.powershell """
            cd ${scm_checkout_dir}
            Copy-Item -Path 'pfe_1_1_0/*' -Destination 'pfe/' -Recurse -force
        """
}

}
