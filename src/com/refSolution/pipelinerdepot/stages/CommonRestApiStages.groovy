
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


    def createBlobAndDesiredState(Map env, Map stageInput = [:]){
        script.stage("Upload blob and create desired state"){
            script.withCredentials([script.$class: 'UsernamePasswordMultiBinding',credentialsId: 'pantaris_tech_user', passwordVariable: "PANT_PASSWORD", usernameVariable: 'PANT_USERNAME']) {
                logger.info("calling python script from groovy")
                script.bat"""
                cd restapi
                py createDesiredState.py ${PANT_PASSWORD}
                """
            }
            
         }
     }

    def listDevicedata(Map env, Map stageInput = [:]){
        logger.info("List devices with device ID")


    } 

    def listVehicledata(Map env, Map stageInput = [:]){
        logger.info("List vehicle data")
    }
 
}
