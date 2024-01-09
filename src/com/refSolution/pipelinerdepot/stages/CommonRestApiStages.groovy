
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
            String buildName = env.JOB_NAME
            String buildNumber = env.BUILD_NUMBER
            String app_name = "opd"
            String app_version = "2"
            String action_type = "install"
            def swpkg_blobId = "india_swpkg_${app_name}_${app_version}_${action_type}_${buildNumber}"
            def vhpkg_blobId = "india_vhpkg_${app_name}_${app_version}_${action_type}_${buildNumber}"
            def desiredStateName = "India_ds_${app_name}_${app_version}_${action_type}_${buildNumber}"
            logger.info(swpkg_blobId)
            logger.info(vhpkg_blobId)
            script.withCredentials([script.usernamePassword(credentialsId: 'pantaris_tech_user', passwordVariable: "PANT_PASSWORD", usernameVariable: 'PANT_USERNAME')]) {
                
                logger.info("calling python script from groovy")
                script.bat"""
                cd restapi
                py createDesiredState.py ${script.PANT_PASSWORD} ${script.PANT_USERNAME} ${swpkg_blobId} ${vhpkg_blobId} ${desiredStateName} ${app_name} ${app_version}
                """
            }
            
         }
     }

    def verifyDeviceStatus(Map env, Map stageInput = [:]){
        script.stage("Verify device status"){
            logger.info("List devices with device ID")
            String deviceId = env.device_Id
                script.withCredentials([script.usernamePassword(credentialsId: 'pantaris_tech_user', passwordVariable: "PANT_PASSWORD", usernameVariable: 'PANT_USERNAME')]) {
                    
                    logger.info("Verifying device status with device ID")
                    script.bat"""
                    cd restapi
                    py pantaris_api.py -c Get_Device_List -d_id ${deviceId} -c_s ${script.PANT_PASSWORD} -c_id ${script.PANT_USERNAME}
                    """
                }

        }


    } 


    def verifyVehicleStatus(Map env, Map stageInput = [:]){
        script.stage("Verify vehicle status"){
            logger.info("List vehicle with vehicle ID")
            String vehicleId = env.vehicle_Id
                script.withCredentials([script.usernamePassword(credentialsId: 'pantaris_tech_user', passwordVariable: "PANT_PASSWORD", usernameVariable: 'PANT_USERNAME')]) {
                    
                    logger.info("Verifying vehicle status with vehicle ID")
                    script.bat"""
                    cd restapi
                    py vehicleDetails.py -c Get_vehicle_list -v_id ${vehicleId} -c_s ${script.PANT_PASSWORD} -c_id ${script.PANT_USERNAME}
                    """
                }

        }


    } 

    def verifyblob(Map env, Map stageInput = [:]){
        script.stage("verify blob with ID"){
            String blobId = stageInput.blob_Id
            String deviceId = stageInput.device_Id
            logger.info(blobId)
            logger.info(deviceId)
                script.withCredentials([script.usernamePassword(credentialsId: 'pantaris_tech_user', passwordVariable: "PANT_PASSWORD", usernameVariable: 'PANT_USERNAME')]) {
                    
                    logger.info("verify  and get blob data with ID")
                    script.bat"""
                    cd restapi
                    py pantaris_api.py -c Blob_Meta_Info -b_id Tk_AVH_app_test_install -c_s ${script.PANT_PASSWORD} -c_id ${script.PANT_USERNAME}
                    """
                }
        }
    }
 
}
