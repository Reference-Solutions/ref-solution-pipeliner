package com.refSolution.pipelinerdepot.utils

import com.bosch.pipeliner.LoggerDynamic
import com.cloudbees.groovy.cps.NonCPS

/**
 * This class provides helper functions to improve Jenkins scripting
 *

 */

public class Dac {
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
    public Dac(def script, def env) {
        this.script = script
        this.logger = new LoggerDynamic(script)
    }

    def sphinxBuildDac (String docsPath){
        //logger.info("calling Dac from Utils")
        script.bat """
            call $docsPath/make.bat clean
            call $docsPath/make.bat html  
        """
    }
    def ghPublishDac (String docsurl,String ghFolder, String html_source_path, String html_destination_path){
        logger.info("Dac publish ")
        script.withCredentials([script.usernamePassword(credentialsId: 'DAC', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
            script.bat """
                git clone ${docsurl}
                robocopy ${html_source_path} ${html_destination_path} /COPYALL /E
                cd ${ghFolder}
                git add .
                git commit -m "commit to publish : "
                git push
            """
       }
    }
}