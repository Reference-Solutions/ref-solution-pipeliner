package com.refSolution.pipelinerdepot.utils

import com.bosch.pipeliner.LoggerDynamic
import com.cloudbees.groovy.cps.NonCPS

/**
 * This class provides helper functions to improve Jenkins scripting
 *

 */

public class RestApi {
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
    public RestApi(def script, def env) {
        this.script = script
        this.logger = new LoggerDynamic(script)
    }

    def sphinxBuildDac (String docsPath){
        
    }
    def ghPublishDac (String docsurl,String ghFolder, String html_source_path, String html_destination_path){
        
    }
}