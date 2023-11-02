package com.refSolution.pipelinerdepot.utils

import com.bosch.pipeliner.LoggerDynamic
import com.cloudbees.groovy.cps.NonCPS
import groovy.io.FileType

/**
 * This class provides conan functions
 *

 */


public class Nexus {
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
    public Nexus(def script, def env) {
        this.script = script
        this.logger = new LoggerDynamic(script)
    }

    def upload(
        String nexusVersion,
        String protocol,
        String nexusUrl,
        String groupId,
        String version,
        String repository,
        String credentialsId,
        String projectName,
        String classifier,
        String filePattern,
        String packaging
    ) {
        // Find files based on the provided regex pattern
        def filesToUpload = script.findFiles(glob: filePattern)
        
        // Now, iterate over the filtered files and upload each one
        filesToUpload.each { filePath ->
            filePath =  filePath.getPath()
            script.nexusArtifactUploader(
                nexusVersion: nexusVersion,
                protocol: protocol,
                nexusUrl: nexusUrl,
                groupId: groupId,
                version: version,
                repository: repository,
                credentialsId: credentialsId,
                artifacts: [
                    [
                        artifactId: projectName,
                        classifier: classifier,
                        file: filePath,
                        type: packaging
                    ]
                ]
            )
        }
    }

    // Helper function to find files
    // def findFiles(Map<String, String> options) {
    //     def files = []
    //     options.each { key, value ->
    //         if (key == 'glob') {
    //             files = findFiles(value)
    //         }
    //     }
    //     return files
    // }


    def download(
        String nexusUrl,
        String repository,
        String groupId,
        String projectName,
        String version,
        String credentialsId,
        String packaging,
        String targetDirectory = "target",
        String fileNameRegex = null
    ) {
        def nexusArtifactUrl = "${nexusUrl}/repository/${repository}/${groupId}/${projectName}/${version}/${projectName}-${version}.${packaging ?: 'jar'}"

        script.withCredentials([script.usernamePassword(credentialsId: credentialsId, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
            if (fileNameRegex) {
                script.sh "curl -o ${targetDirectory}/ -u $USERNAME:$PASSWORD --fail --create-dirs ${nexusArtifactUrl} -O -# --write-out '%{filename_effective}' | grep -E '${fileNameRegex}'"
            } else {
                script.sh "curl -o ${targetDirectory}/${projectName}-${version}.${packaging ?: 'jar'} -u $USERNAME:$PASSWORD ${nexusArtifactUrl}"
            }
        }
    }
}


