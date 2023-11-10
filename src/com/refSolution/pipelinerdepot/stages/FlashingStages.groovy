package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic


/**
* Contains stages that can be reused across pipelines
*/
class FlashingStages {

    private def script
    private Map env
    private LoggerDynamic logger

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    FlashingStages(script, Map env) {
        this.script = script
        this.env = env
        this.logger = new LoggerDynamic(script)
    }

    def stageVerifyT32(Map env, Map stageInput = [:]){
        script.stage("Check if T32app is running") {
            script.bat """
            echo 'Testing flashing'
            """
        }
    }

    def stageFlashing(Map env, Map stageInput = [:]){
        script.stage("Flashing to VIP Board") {
            String arcbsw_binary_path = stageInput.arcbsw_binary_dir?.trim() ?: ''
            String qnx_binary_path = stageInput.qnx_binary_dir?.trim() ?: ''
            String scm_checkout_dir = stageInput.custom_scm_checkout_dir?.trim() ?: ''
            script.bat """
            cd ${scm_checkout_dir}
            echo 'Flashing to VIP Board...'
            FlashVIP.bat "M7 + QNX" "${arcbsw_binary_path}" "${qnx_binary_path}"
            """
        }
    }
}
