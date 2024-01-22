
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.refSolution.pipelinerdepot.utils.ota.SwPackage
import com.refSolution.pipelinerdepot.utils.ota.pantaris

/**
* Contains stages that can be reused across pipelines
*/
class OtaNgStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private SwPackage swPackage
    private Pantaris pantaris
    
    private def appName
    private def appVersion
    private def actionType
    private def packageName

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    OtaNgStages(script, Map env) {
        this.script = script
        this.env = env
        this.swPackage = new SwPackage(script, env)
        this.pantaris = new Pantaris(script,env)
        this.logger = new LoggerDynamic(script)
    }
    
    def stageSwPackgeCreation(Map env, Map stageInput = [:]){
        script.stage("SW Package Creation") {
            
            String swcDirPath = stageInput.swc_dir_path?.trim() ?: 'ota-ng/swpkg/install_swc/'
            String genSwpDirPath = stageInput.gen_swp_dir_path?.trim() ?: 'ota-ng/swpkg/gen_swp_fb/'
            String appDirPath = stageInput.app_dir_path.trim()
            String vrtefsToolPath = stageInput.vrtefs_tool_path?.trim() ?: 'C:/toolbase/vrte_adaptive_studio/r23-08/vrte_fs/'
            
            appName = stageInput.app_name.trim()
            appVersion = stageInput.app_version?.trim() ?: '1.0.0'
            actionType =  stageInput.action_type?.trim() ?: 'INSTALL'
            packageName = "${actionType.toLowerCase()}_swc_app".toLowerCase()
            
            swPackage.swPackgeCreation(swcDirPath, genSwpDirPath, appDirPath, vrtefsToolPath, appName, appVersion, actionType, packageName)
        }
    }

    def stageVehicePackgeCreation(Map env, Map stageInput = [:]){
        script.stage("Vehice Package Creation") {

            String vpkgDirPath = stageInput.vpkg_dir_path?.trim() ?: 'ota-ng/vpkg/'
            swPackage.vehicePackgeCreation(vpkgDirPath, packageName, appName)
        }
    }

    def stageDesiredStateCreation(Map env, Map stageInput = [:]){
        script.stage("Desired State Creation") {
            String buildName = env.JOB_NAME
            String buildNumber = env.BUILD_NUMBER
            String appName = stageInput.app_name.trim()
            String appVersion = stageInput.app_version?.trim() ?: '1.0.0'
            String actionType = stageInput.action_type?.trim() ?: 'INSTALL'
            String swpkgFile2Upload = stageInput.swpkg_file_upload?.trim() ?: 'install_swc_app_opd.swpkg'
            String vhpkgFile2Upload = stageInput.vhpkg_file_upload?.trim() ?: 'vehiclepkg_install_swc_app_opd.tar'
            String scriptPath = stageInput.script_path?.trim() ?: 'ota-ng/pantaris/scripts'
            String swpkgBlobId = "india_swpkg_${appName}_${appVersion}_${actionType}_${buildNumber}"
            String vhpkgBlobId = "india_vhpkg_${appName}_${appVersion}_${actionType}_${buildNumber}"
            String desiredStateName = "India_ds_${appName}_${appVersion}_${actionType}_${buildNumber}"
            pantaris.createBlobAndDesiredState(scriptPath, swpkgBlobId, vhpkgBlobId, desiredStateName, appName, appVersion, swpkgFile2Upload, vhpkgFile2Upload)    
        }
    }

    def stageDeviceCreation(Map env, Map stageInput = [:]){
        script.stage("Device Creation") {
            logger.info("List devices with device ID")
            String deviceId = stageInput.device_Id?.trim() ?: 'deviceIdtest2'
            String scriptPath = stageInput.script_path?.trim() ?:'ota-ng/pantaris/scripts'
                script.withCredentials([script.usernamePassword(credentialsId: 'pantaris_tech_user', passwordVariable: "PANT_PASSWORD", usernameVariable: 'PANT_USERNAME')]) {
                    
                    logger.info("Verifying device status with device ID")
                    script.bat"""
                    py ${scriptPath}/pantaris_api.py -c Get_Device_List -d_id ${deviceId} -c_s ${script.PANT_PASSWORD} -c_id ${script.PANT_USERNAME}
                    """
                }    
        }
    }

     def stageVehicleCreation(Map env, Map stageInput = [:]){
        script.stage("Vehicle Creation") {
            logger.info("List vehicle with vehicle ID")
            String vehicleId = stageInput.vehicle_Id?.trim() ?: '43717197-bc95-49bf-b82f-1505d33c14b2'
            String scriptPath = stageInput.script_path?.trim() ?:'ota-ng/pantaris/scripts'
                script.withCredentials([script.usernamePassword(credentialsId: 'pantaris_tech_user', passwordVariable: "PANT_PASSWORD", usernameVariable: 'PANT_USERNAME')]) {
                    
                    logger.info("Verifying vehicle status with vehicle ID")
                    script.bat"""
                    py ${scriptPath}/vehicleDetails.py -c Get_vehicle_list -v_id ${vehicleId} -c_s ${script.PANT_PASSWORD} -c_id ${script.PANT_USERNAME}
                    """
                }     
        }
    }

}
