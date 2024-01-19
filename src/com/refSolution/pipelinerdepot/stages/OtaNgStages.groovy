
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.bosch.pipeliner.ScriptUtils
import java.io.File
import com.refSolution.pipelinerdepot.utils.GhCli


/**
* Contains stages that can be reused across pipelines
*/
class OtaNgStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private ScriptUtils utils
    private def swPackageSize
    private def appName
    private def appVersion
    private def actionType

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    OtaNgStages(script, Map env) {
        this.script = script
        this.env = env
        this.utils = new ScriptUtils(script, env)
        this.logger = new LoggerDynamic(script)
    }
    
    def stageDownloadApplication(Map env, Map stageInput = [:]){
            script.stage("Download Releases from Github") {
                String releaseTag = stageInput.Github_releaseTag?.trim()
                String owner = stageInput.Github_owner?.trim()        
                String repo = stageInput.Github_repo?.trim()
                String pattern = stageInput.Github_pattern?.trim()

                GhCli.GhCliReleaseDownload(releaseTag, owner, repo, pattern)
        }
    }
    def stageSwPackgeCreation(Map env, Map stageInput = [:]){
        script.stage("SW Package Creation") {
            
            String swcDirPath = stageInput.swc_dir_path?.trim() ?: 'ota-ng/swpkg/install_swc/'
            String genSwpDirPath = stageInput.gen_swp_dir_path?.trim() ?: 'ota-ng/swpkg/gen_swp_fb/'
            String appDirPath = stageInput.app_dir_path.trim()
            String vrtefsToolPath = stageInput.vrtefs_tool_path?.trim() ?: 'C:/toolbase/vrte_adaptive_studio/r23-10/vrte_fs/'
            
            appName = stageInput.app_name.trim()
            appVersion = stageInput.app_version?.trim() ?: '1.0.0'
            actionType =  stageInput.action_type?.trim() ?: 'INSTALL'

            updateArxmlWithAppDetails(swcDirPath + "software_cluster.arxml")
            updateArxmlWithAppDetails(swcDirPath + "software_package.arxml")
            generateFlatbufferConfigFiles(swcDirPath, vrtefsToolPath.trim())
            removeKeyFromFlatCfgJson(swcDirPath + "gen/${appName}/upd__${appName}_flatcfg.json")
            copyfilestoGenerateSWPackageFolder(swcDirPath, appDirPath, genSwpDirPath )
            createSWPackageBinary(genSwpDirPath)
        }
    }

    def stageVehicePackgeCreation(Map env, Map stageInput = [:]){
        script.stage("Vehice Package Creation") {
            String vpkgDirPath = stageInput.vpkg_dir_path?.trim() ?: 'ota-ng/vpkg/'
            vehicePackgeCreation(vpkgDirPath)
        }
    }

    def updateArxmlWithAppDetails(def filePath){
        if (script.fileExists(filePath)) {
            // Read the file content
            def fileContent = script.readFile(file: filePath).trim()
            
            // Replace all occurrences of the search string
            fileContent = fileContent.replaceAll("__APP_NAME__", appName)
            fileContent = fileContent.replaceAll("__APP_VERSION__", appVersion)
            fileContent = fileContent.replaceAll("__ACTION_TYPE__", actionType)
            
            // Write the modified content back to the file
            script.writeFile(file: filePath, text: fileContent)

            logger.info("Updated Application Details in SW cluster and package Arxml files.")
        }
        else{
            logger.error("file : $filePath not exists")
        }
    }

    def generateFlatbufferConfigFiles(def swcDirPath, def vrtefsToolPath){
        logger.info("Flatbuffer config files Generation Started...")
        script.bat"""
            cd ${swcDirPath}
            ${vrtefsToolPath}vrte_fs -v -b . -fp **.arxml flatbuffers -o gen
        """
        
        logger.info("Flatbuffer config files Generation Completed...")
    }

    def removeKeyFromFlatCfgJson(def jsonFilePath){        
        
        def keyToRemove = 'keyValueStorage'

        // Read the JSON file
        def jsonData = script.readJSON file: jsonFilePath
        
        jsonData.Process[0]["keyValueStorageMapping"][0].remove(keyToRemove)

        // Write the modified JSON back to the file
        script.writeJSON file: jsonFilePath, json: jsonData
    }

    def copyfilestoGenerateSWPackageFolder(def swcDirPath, def appDirPath, def genSwpDirPath){
        script.sh """
            cp ${swcDirPath}gen/${appName}/upd__${appName}_flatcfg.json ${genSwpDirPath}
            cp ${swcDirPath}gen/upd_flatcfg.fbs ${genSwpDirPath}
            cp ${appDirPath}${appName} ${genSwpDirPath}
        """
    }

    def createSWPackageBinary(def genSwpDirPath){
        script.stash includes: "${genSwpDirPath}**", name: 'gen_swp_fb'
        script.node("ubuntu-lab-pc"){
            script.unstash "gen_swp_fb"
            script.sh """
                cd ${genSwpDirPath}
                python gen_swp_fb.py --container-format 0x0106 --update-manifest-data upd__${appName}_flatcfg.json --update-manifest-schema upd_flatcfg.fbs --artefact ${appName} compressWhole --compress zlib --key-store keys/ --block-size 5000 --verification On --flatc-path flatc --estimated-speed 1024
                tar -cvf app_${appName}_${actionType}_v${appVersion}.swpkg swpkg.bin
            """
            swPackageSize = script.sh(script: "stat -c %s ${genSwpDirPath}app_${appName}_${actionType}_v${appVersion}.swpkg", returnStdout: true).trim()
            script.stash includes: "${genSwpDirPath}app_${appName}_${actionType}_v${appVersion}.swpkg", name: 'swpkg'
            script.archiveArtifacts "${genSwpDirPath}app_${appName}_${actionType}_v${appVersion}.swpkg"
        }
        script.unstash "swpkg"
    }

    def vehicePackgeCreation(def vpkgDirPath){
        
        updateVechicleManifestFile("${vpkgDirPath}vehicle_package_manifest.json", swPackageSize)
        script.sh """
            cd ${vpkgDirPath}
            tar -cvf signedcontainer.tar vehicle_package_manifest.json
            touch signature.crt
            tar -cvf vehiclepkg_app_${appName}_${actionType}_v${appVersion}.tar signedcontainer.tar signature.crt
        """
        script.archiveArtifacts "${vpkgDirPath}vehiclepkg_app_${appName}_${actionType}_v${appVersion}.tar"
    }

    def updateVechicleManifestFile(def filePath, def swPackageSize){
        if (script.fileExists(filePath)) {
            // Read the file content
            def fileContent = script.readFile(file: filePath).trim()
            
            // Replace all occurrences of the search string
            fileContent = fileContent.replaceAll("__SW_PACKAGE_SIZE__", swPackageSize.trim())
            
            // Write the modified content back to the file
            script.writeFile(file: filePath, text: fileContent)

            logger.info("Updated AVehicle Manifest Json file.")
        }
        else{
            logger.error("file : $filePath not exists")
        }
    }
}
