package com.refSolution.pipelinerdepot.utils

import com.bosch.pipeliner.LoggerDynamic
import com.cloudbees.groovy.cps.NonCPS

/**
 * This class provides conan functions
 *

 */


public class Conan {
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
    public Conan(def script, def env) {
        this.script = script
        this.logger = new LoggerDynamic(script)
    }


    void upload(String remoteName, String packageRef) {
        try {
            def conanCommand = "conan upload ${packageRef} --all --remote=${remoteName}"
            
            if (script.isUnix()){
                script.sh conanCommand
            }else{
                script.bat conanCommand
            }
        } catch (Exception e) {
            // Handle exceptions, you can log, print an error message, or take other actions
            error "Conan upload failed: ${e.message}"
        }
    }
}
