package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.bosch.pipeliner.ScriptUtils
import com.refSolution.pipelinerdepot.stages.CommonStages
import com.refSolution.pipelinerdepot.stages.QnxStages


/**
* Contains stages that can be reused across pipelines
*/
class CommonMplStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private ScriptUtils utils
    private CommonStages commonStages
    private QnxStages qnxStages 
    
   

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    CommonMplStages(script, Map env) {
        this.script = script
        this.env = env
        this.logger = new LoggerDynamic(script)
        this.utils = new ScriptUtils(script, env)
        this.commonStages = new CommonStages(script, env)
        this.qnxStages = new QnxStages(script, env)
       
       
    }

    // def stageBuild(Map env, Map stageInput = [:]){
      	
    //   	String pfeCopy = stageInput.pfe_copy?.trim() ?: 'false'
    //     if(pfeCopy == "true"){
    //         script.stage("Copy PFE") {  
    //           copyPFE(env, stageInput)
    //         }
    //     }
    //     script.stage("Build") {
    //         makeBuild(env, stageInput)
    //     }
    // }

        def stageBuild(Map env, Map stageInput = [:]){
        qnxStages.stagebuild(env, stageInput)   

        } 

    
        def makeBuild(Map env, Map stageInput = [:]) {
        // Call QnxStages methods
        //qnxStage.stagebuild(env, stageInput)
        qnxStages.makeBuild(env, stageInput)
        //qnxStages.copyPFE(env, stageInput)
        }

        def copyPFE(Map env, Map stageInput = [:]) {
        qnxStages.copyPFE(env, stageInput)   

        } 



    //     def makeBuild(Map env, Map stageInput = [:]){
    //     String qnxSdkPath = stageInput.qnx_sdk_path?.trim() ?: 'C:/Users/zrd2kor/qnx710'
    //     String scm_checkout_dir = stageInput.custom_scm_checkout_dir?.trim() ?: ''
    //     script.bat """
    //         echo 'Set QNX env variable'
    //         call ${qnxSdkPath}/qnxsdp-env.bat
    //         echo 'starting building'
    //         cd ${scm_checkout_dir}
    //         make all
    //     """

    //     }


    //     def copyPFE(Map env, Map stageInput = [:]){
    //     String scm_checkout_dir = stageInput.custom_scm_checkout_dir?.trim() ?: 'C:/Users/zrd2kor/qnx710'
    //     script.powershell """
    //         cd ${scm_checkout_dir}
    //         Copy-Item -Path 'pfe_1_1_0/*' -Destination 'pfe/' -Recurse -force
    //     """
    // }


  
}


