package com.refSolution.pipelinerdepot.utils

import com.bosch.pipeliner.LoggerDynamic
import com.cloudbees.groovy.cps.NonCPS

/**
 * This class provides helper functions to improve Jenkins scripting
 *

 */

public class GhCli {
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
    public GhCli(def script, def env) {
        this.script = script
        this.logger = new LoggerDynamic(script)
    }

    def pullArtifactfromRelease(def releasetag, def owner, def repo, def pattern , def appName, def appPath, def appFolder, def patToken){
        script.sh """
            gh auth login -h github.boschdevcloud.com --with-token ${patToken}
            gh auth status

            gh release download ${releasetag} -R ${owner}/${repo} --pattern ${pattern} --clobber
            unzip -o ${pattern} -d \${WORKSPACE} 
            unzip -o ${appFolder} -d \${WORKSPACE}
            cd ${appPath}
            cp -r ${appName} \${WORKSPACE}

            """
    }
}