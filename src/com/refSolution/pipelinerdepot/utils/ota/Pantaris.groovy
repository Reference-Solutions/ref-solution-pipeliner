package com.refSolution.pipelinerdepot.utils.ota

import com.bosch.pipeliner.LoggerDynamic
import com.cloudbees.groovy.cps.NonCPS

/**
 * This class provides helper functions to improve Jenkins scripting
 *

 */

public class Pantaris {
    /**
     * The script object instance from Jenkins
     */
    private def script
    /**
     * Logger object. Needs to be dynamic to display messages after the Jenkins master restart.
     */
    private LoggerDynamic logger
    
    private Map env

    private def appName
    private def appVersion
    private def actionType
    private def packageName

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Reference to the Jenkins environment
     */
    public Pantaris(def script, def env) {
        this.script = script
        this.env = env
        this.logger = new LoggerDynamic(script)
    }

    def createBlobAndDesiredState(String scriptPath,String swpkgBlobId,String vhpkgBlobId,String desiredStateName,String appName,String appVersion,String swpkgFile2Upload,String vhpkgFile2Upload){
        logger.info("creating blob and desired state in progress")
        script.withCredentials([script.usernamePassword(credentialsId: 'pantaris_tech_user', passwordVariable: "PANT_PASSWORD", usernameVariable: 'PANT_USERNAME')]) {
                
                logger.info("calling python script from groovy")
                script.bat"""
                py ${scriptPath}/createDesiredState.py ${script.PANT_PASSWORD} ${script.PANT_USERNAME} ${swpkgBlobId} ${vhpkgBlobId} ${desiredStateName} ${appName} ${appVersion} ${swpkgFile2Upload} ${vhpkgFile2Upload}
                """
            } 


    }

    def verifyDeviceStatus(String scriptPath,String deviceId){
        logger.info("verification of device in progress")
        script.node("Karthick"){
            script.withCredentials([script.usernamePassword(credentialsId: 'pantaris_tech_user', passwordVariable: "PANT_PASSWORD", usernameVariable: 'PANT_USERNAME')]) {
                        
                        logger.info("Verifying device status with device ID")
                        script.bat"""
                        py ${scriptPath}/pantaris_api.py -c Get_Device_List -d_id ${deviceId} -c_s ${script.PANT_PASSWORD} -c_id ${script.PANT_USERNAME}
                        """
                    } 
        }           

    }

    def verifyVehicleStatus(String scriptPath,String vehicleId){
        logger.info("verification of vehicle in progress")
        script.node("Karthick"){
            script.withCredentials([script.usernamePassword(credentialsId: 'pantaris_tech_user', passwordVariable: "PANT_PASSWORD", usernameVariable: 'PANT_USERNAME')]) {
                        
                        logger.info("Verifying vehicle status with vehicle ID")
                        script.bat"""
                        py ${scriptPath}/vehicleDetails.py -c Get_vehicle_list -v_id ${vehicleId} -c_s ${script.PANT_PASSWORD} -c_id ${script.PANT_USERNAME}
                        """
                    }  
        }           

    }

 
}