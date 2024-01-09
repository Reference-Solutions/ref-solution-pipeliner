
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.refSolution.pipelinerdepot.utils.RestApi

/**
* Contains stages that can be reused across pipelines
*/
class CommonRestApiStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private RestApi restapi

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    CommonRestApiStages(script, Map env) {
        this.script = script
        this.env = env
        this.restapi = new RestApi(script, env)
        this.logger = new LoggerDynamic(script)
    }

    def getDesirtedStateByName(Map env, Map stageInput = [:]){
        script.stage("getDesirtedStateByName"){
            logger.info("getDesirtedStateByName")
            String tToken = stageInput.accessToken
            def command = ["curl", "-X", "GET", "-H", "Authorization: Bearer $tToken", "https://ota.eu.bosch-mobility-cloud.com/api/applications/ota/desiredStates/Test2__AVH_app_test_install"]
            def generatedResponse = command.execute().text
        }
        
    }
    def method2(Map env, Map stageInput = [:]){
        script.stage("method2 stage"){
            logger.info("method2")
        }
        
    
    }
}
