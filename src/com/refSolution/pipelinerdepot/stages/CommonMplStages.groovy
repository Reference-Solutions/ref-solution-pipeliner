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

      def stageBuild(Map env, Map stageInput = [:]){
          commonStages.stageBuild(env, [:])
          }
      def makeBuild(Map env, Map stageInput = [:]) {
          commonStages.makeBuild(env, stageInput)
    
             }
      def copyPFE(Map env, Map stageInput = [:]) {
          commonStages.copyPFE(env, stageInput)
             
             }
    
  
}


