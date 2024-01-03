package com.refSolution.pipelinerdepot.utils

import com.bosch.pipeliner.LoggerDynamic
import com.cloudbees.groovy.cps.NonCPS
import groovy.io.FileType

/**
 * This class provides helper functions to improve Jenkins scripting
 *

 */

public class Versioning {
    /**
     * The script object instance from Jenkins
     */
    private def script
    /**
     * Logger object. Needs to be dynamic to display messages after the Jenkins master restart.
     */
    private LoggerDynamic logger

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Reference to the Jenkins environment
     */
    public Versioning(def script, def env) {
        this.script = script
        this.logger = new LoggerDynamic(script)
    }

    void versioning(String pattern, String version){
        logger.info("pattern : " + pattern)
        logger.info("version : " + version)
        versioningArtifacts(pattern, version)
        updateBuildDisplay(version)
        logger.info("Versioning process completed ")
    }

    void versioningArtifact(String pattern, String version){

        // Get a list of all files in the specified directory and its subdirectories
        def fileList = script.findFiles(glob: pattern)

        // Iterate through the files and rename them
        fileList.each { file ->
            def oldPath = file.getPath()
            // Get the file extension
            def extension = file.name.lastIndexOf('.') > 0 ? file.name.substring(file.name.lastIndexOf('.') + 1) : ''

            // Define a new name for the file with the version
            def newName = "${file.name.substring(0, file.name.lastIndexOf('.'))}_${version}.${extension}"

            def newPath = oldPath.replace(file.name,newName)
            
            if (script.isUnix()){
                script.sh "mv $file $newPath" 
            }
            else {
                script.bat "mv $file $newPath"
            }
        }
    }

    void updateBuildDisplay(String version){
        def displayName = "#${script.BUILD_NUMBER}, ${script.JOB_NAME}, ${version}"

        logger.info("Build Display Name Before: " + script.currentBuild.displayName)

        script.currentBuild.displayName = displayName
        logger.info("Build Display Name After: " + script.currentBuild.displayName)
    }

    String buildDate(String formatString = "yyyyMMddHHmmss") {
        return new Date(script.currentBuild.getStartTimeInMillis()).format(formatString).toString()
    }
}