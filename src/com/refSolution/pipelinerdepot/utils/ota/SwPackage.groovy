package com.refSolution.pipelinerdepot.utils.ota

import com.bosch.pipeliner.LoggerDynamic
import com.cloudbees.groovy.cps.NonCPS

/**
 * This class provides helper functions to improve Jenkins scripting
 *

 */

public class SwPackage {
    /**
     * The script object instance from Jenkins
     */
    private def script
    /**
     * Logger object. Needs to be dynamic to display messages after the Jenkins master restart.
     */
    private LoggerDynamic logger
    
    private Map env

    private def swPackageSize
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
    public SwPackage(def script, def env) {
        this.script = script
        this.env = env
        this.logger = new LoggerDynamic(script)
    }

    def swPackgeCreation(def swcDirPath, def genSwpDirPath, def appDirPath, def vrtefsToolPath, def swPackageAppName, def swPackageAppVersion, def swPackageActionType, def swPackageName){
        
        appName = swPackageAppName
        appVersion = swPackageAppVersion
        actionType = swPackageActionType
        packageName = swPackageName

        logger.info("SW package Creation Started...")

        updateArxmlWithAppDetails(swcDirPath + "software_cluster.arxml")
        updateArxmlWithAppDetails(swcDirPath + "software_package.arxml")
        generateFlatbufferConfigFiles(swcDirPath, vrtefsToolPath.trim())
        removeKeyFromFlatCfgJson(swcDirPath + "gen/${packageName}/upd__${packageName}_flatcfg.json")
        copyfilestoGenerateSWPackageFolder(swcDirPath, appDirPath, genSwpDirPath )
        createSWPackageBinary(genSwpDirPath)

        logger.info("SW package Creation Completed...")
    }

    def updateArxmlWithAppDetails(def filePath){
        if (script.fileExists(filePath)) {
            // Read the file content
            def fileContent = script.readFile(file: filePath).trim()
            
            logger.info("appName = $appName")
            logger.info("appVersion = $appVersion")
            logger.info("actionType = $actionType")
            logger.info("packageName = $packageName")
                
            // Replace all occurrences of the search string
            fileContent = fileContent.replaceAll("__APP_PACKAGE_NAME__", packageName)
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
            cp ${swcDirPath}gen/${packageName}/upd__${packageName}_flatcfg.json ${genSwpDirPath}
            cp ${swcDirPath}gen/upd_flatcfg.fbs ${genSwpDirPath}
            cp ${appDirPath}${appName} ${genSwpDirPath}
        """
    }

    def createSWPackageBinary(def genSwpDirPath){
        script.stash includes: "${genSwpDirPath}**", name: 'gen_swp_fb'
        script.node("ubuntu-lab-pc"){
            script.cleanWs()
            script.unstash "gen_swp_fb"
            script.sh """
                cd ${genSwpDirPath}
                tar -r --numeric-owner --owner=0 --group=0 -f ${packageName}_1_random_data.tar ${appName}
                python3 gen_swp_fb.py --container-format 0x0106 --update-manifest-schema upd_flatcfg.fbs --update-manifest-data upd__${packageName}_flatcfg.json --artefact ${packageName}_1_random_data.tar compressChunks --key-store keys/ --compress None --output ${packageName}_${appName}.swpkg --flatc-path flatc --estimated-speed 15360 --block-size 64535
            """
            swPackageSize = script.sh(script: "stat -c %s ${genSwpDirPath}${packageName}_${appName}.swpkg", returnStdout: true).trim()
            script.stash includes: "${genSwpDirPath}${packageName}_${appName}.swpkg", name: 'swpkg'
            script.archiveArtifacts "${genSwpDirPath}${packageName}_${appName}.swpkg"
        }
        script.unstash "swpkg"
    }

    def vehicePackgeCreation(def vpkgDirPath, def packageName, def appName){
        
        updateVechicleManifestFile("${vpkgDirPath}vehicle_package_manifest.json", swPackageSize)
        script.sh """
            cd ${vpkgDirPath}
            tar -cvf signedcontainer.tar vehicle_package_manifest.json
            touch signature.crt
            tar -cvf vehiclepkg_${packageName}_${appName}.tar signedcontainer.tar signature.crt
        """
        script.archiveArtifacts "${vpkgDirPath}vehiclepkg_${packageName}_${appName}.tar"
    }

    def updateVechicleManifestFile(def filePath, def swPackageSize){
        if (script.fileExists(filePath)) {
            // Read the file content
            def fileContent = script.readFile(file: filePath).trim()
            
            // Replace all occurrences of the search string
            fileContent = fileContent.replaceAll("__SW_PACKAGE_SIZE__", swPackageSize.trim())
            
            // Write the modified content back to the file
            script.writeFile(file: filePath, text: fileContent)

            logger.info("Updated A Vehicle Manifest Json file.")
        }
        else{
            logger.error("file : $filePath not exists")
        }
    }
}