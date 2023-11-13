package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.bosch.pipeliner.ScriptUtils


/**
* Contains stages that can be reused across pipelines
*/
class FlashingStages {

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
    FlashingStages(script, Map env) {
        this.script = script
        this.env = env
        this.logger = new LoggerDynamic(script)
        this.utils = new ScriptUtils(script, env)
    }

    def stageVerifyT32(Map env, Map stageInput = [:]) {
        powershell '''
        $process = Get-Process -Name "t32marm"
        if ($process -ne $null) {
            $process.Kill()
            Write-Host "The app is not running!!"
            Start-Sleep -Seconds 30
        }
        else {
            Write-Host "The app is not running."
        }
    '''
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
