package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.bosch.pipeliner.ScriptUtils



/**
* Contains stages that can be reused across pipelines
*/
class CommonMplStages {

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
    CommonMplStages(script, Map env) {
        this.script = script
        this.env = env
        this.logger = new LoggerDynamic(script)
        this.utils = new ScriptUtils(script, env)
        this.commonStages = new CommonStages(script, env)
       
       
    }

    // def vrtePull(Map env, Map stageInput = [:]) {
    //     // add vrte pull stages here
    //     script.echo "Vrte pull"
    // }

    //   def stageBuild(Map env, Map stageInput = [:]){
    //       commonStages.stageBuild(env, [:])
         // }
    //   def makeBuild(Map env, Map stageInput = [:]) {
    //       commonStages.makeBuild(env, stageInput)
    
    //          }
    //   def copyPFE(Map env, Map stageInput = [:]) {
    //       commonStages.copyPFE(env, stageInput)
             
    //          }
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

       def copyPFE(Map env, Map stageInput = [:]){
        String scm_checkout_dir = stageInput.custom_scm_checkout_dir?.trim() ?: 'C:/Users/zrd2kor/qnx710'
        script.powershell """
            cd ${scm_checkout_dir}
            Copy-Item -Path 'pfe_1_1_0/*' -Destination 'pfe/' -Recurse -force
        """
    }
    
  
}


