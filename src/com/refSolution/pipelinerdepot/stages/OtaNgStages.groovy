
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.refSolution.pipelinerdepot.utils.ota.SwPackage
import com.refSolution.pipelinerdepot.utils.ota.Pantaris
import com.refSolution.pipelinerdepot.utils.GhCli


/**
* Contains stages that can be reused across pipelines
*/
class OtaNgStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private SwPackage swPackage
    private Pantaris pantaris
    private GhCli ghCli
    
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
        this.pantaris = new Pantaris(script, env)
        this.ghCli = new GhCli(script, env)
        this.logger = new LoggerDynamic(script)
    }

    def stagePullAppFromGithub(Map env, Map stageInput = [:]){
        script.stage("Pull Application") { 
            String releaseTag = stageInput.gh_release_tag?.trim()
            String owner = stageInput.gh_owner?.trim()
            String repo = stageInput.gh_repo?.trim()
            String pattern = stageInput.gh_pattern?.trim()
            String zipSubFolder = stageInput.app_zip_sub_folder?.trim()

            ghCli.pullArtifactfromRelease( releaseTag, owner, repo, pattern)

            script.sh """
                unzip ${pattern}
                unzip ${zipSubFolder}
            """
        }
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
            String scriptPath = stageInput.pantaris_script_path?.trim()
            String genSwpDirPath = stageInput.gen_swp_dir_path?.trim()
            String vpkgDirPath = stageInput.vpkg_dir_path?.trim()

            String swpkgBlobId = "india_swpkg_${appName}_${appVersion}_${actionType}_${buildNumber}"
            String vhpkgBlobId = "india_vhpkg_${appName}_${appVersion}_${actionType}_${buildNumber}"
            String desiredStateName = "DS_${appName}_${actionType}_${buildNumber}"
            String swpkgFile2Upload = "${genSwpDirPath}${packageName}_${appName}.swpkg"
            String vhpkgFile2Upload = "${vpkgDirPath}vehiclepkg_${packageName}_${appName}.tar"

            pantaris.createBlobAndDesiredState(scriptPath, swpkgBlobId, vhpkgBlobId, desiredStateName, appName, appVersion, swpkgFile2Upload, vhpkgFile2Upload)    
        }
    }

    def stageDeviceCreation(Map env, Map stageInput = [:]){
        script.stage("Verify Device") {
            logger.info("List devices with device ID")
            
            String deviceId = stageInput.device_id?.trim() 
            String scriptPath = stageInput.pantaris_script_path?.trim()
            
            pantaris.verifyDeviceStatus(scriptPath, deviceId)  
        }
    }

     def stageVehicleCreation(Map env, Map stageInput = [:]){
        script.stage("Verify Vehicle") {
            logger.info("List vehicle with vehicle ID")
           
            String vehicleId = stageInput.vehicle_id?.trim()
            String scriptPath = stageInput.pantaris_script_path?.trim()
            
            pantaris.verifyVehicleStatus(scriptPath, vehicleId)        
        }
    }
    
}
