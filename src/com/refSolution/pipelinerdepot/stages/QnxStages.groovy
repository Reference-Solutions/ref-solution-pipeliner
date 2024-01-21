package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.bosch.pipeliner.ScriptUtils
import com.refSolution.pipelinerdepot.stages.CommonStages


/**
* Contains stages that can be reused across pipelines
*/
class QnxStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private ScriptUtils utils
    private CommonStages commonStages

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    QnxStages(script, Map env) {
        this.script = script
        this.env = env
        this.logger = new LoggerDynamic(script)
        this.utils = new ScriptUtils(script, env)
        this.commonStages = new CommonStages(script, env)
    }
    
    def stageBuild(Map env, Map stageInput = [:]){
      	
        String qnxSdkPath = stageInput.qnx_sdk_path.trim()
        String qnxSrcDir = stageInput.qnx_src_dir.trim()
      	String pfeCopy = stageInput.pfe_copy?.trim() ?: 'false'
        if(pfeCopy == "true"){
            script.stage("QNX Copy PFE") {
                copyPFE(qnxSrcDir)
            }
        }
        script.stage("QNX Build") {
            makeBuild(qnxSdkPath, qnxSrcDir)
        }
    }

    def makeBuild(def qnxSdkPath, def qnxSrcDir){
        script.bat """
            echo 'Set QNX env variable'
            call ${qnxSdkPath}/qnxsdp-env.bat
            echo 'starting building'
            cd ${qnxSrcDir}
            make all
        """
    }

    def copyPFE(def qnxSrcDir){
        script.powershell """
            cd ${qnxSrcDir}
            Copy-Item -Path 'pfe_1_1_0/*' -Destination 'pfe/' -Recurse -force
        """
    }
}
