
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


            def getCommand = ["curl", "-X", "GET", "-H", "Authorization: Bearer $tToken", "https://ota.eu.bosch-mobility-cloud.com/api/applications/ota/desiredStates/Test2__AVH_app_test_install"].execute()
            def generatedResponse = getCommand.waitFor()
            println writeCommand.err.text
            println writeCommand.text
            logger.info("getDesirtedStateByName executed")
            //def generatedResponse = command.execute().text
            logger.info(generatedResponse)
            //def expectedResponse = '{"name":"T2k__AVH_app_test_install","specification":{"domains":[{"id":"avh_test","components":[{"id":"avh_app_test","version":"1.0","config":[{"key":"image","value":"https://api.devices.eu.bosch-mobility-cloud.com/v3/device/blobs/app_avh_install_v1.0.swpkg?token=0727673d-ddb9-4884-9142-f89d6d318921"}]}],"config":[{"key":"image-avh-app-test","value":"https://api.devices.eu.bosch-mobility-cloud.com/v3/device/blobs/vehiclepkg_app_avh_install_v1.0.tar/?token=ef199377-684c-4195-b2d0-2f3aa5857f24"}]}],"baselines":[{"components":["avh:app_demo"],"title":"avh-app-test_k"}]},"createdOn":"2024-01-03T09:44:46.917Z","lastUpdatedOn":"2024-01-03T09:44:46.917Z","_links":{"self":{"href":"https://ota.eu.bosch-mobility-cloud.com/api/applications/ota/desiredStates/Test2__AVH_app_test_install"}}}'
        }
        
    }
    def method2(Map env, Map stageInput = [:]){
        script.stage("method2 stage"){
            logger.info("method2")
        }
        
    
    }
}
