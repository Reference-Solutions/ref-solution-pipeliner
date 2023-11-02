
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.refSolution.pipelinerdepot.utils.ota.SwPackage

/**
* Contains stages that can be reused across pipelines
*/
class OtaNgStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private SwPackage swPackage
    
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
}
