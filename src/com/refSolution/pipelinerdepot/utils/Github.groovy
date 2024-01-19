package com.refSolution.pipelinerdepot.utils
import com.bosch.pipeliner.LoggerDynamic
import com.bosch.pipeliner.ScriptUtils
import com.cloudbees.groovy.cps.NonCPS
import groovy.json.JsonSlurperClassic

/**
 * This class provides helper functions to improve Jenkins scripting
 */

public class Github {
    
    /**
     * The script object instance from Jenkins
     */
    private def script

    /**
     * The env object instance from Jenkins
     */
    private def env

    /**
     * Logger object. Needs to be dynamic to display messages after the Jenkins master restart.
     */
    private LoggerDynamic logger

    
    private ScriptUtils utils

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Reference to the Jenkins environment
     */
    public Github(def script, def env) {
        this.script = script
        this.env = env
        this.logger = new LoggerDynamic(script)
        this.utils = new ScriptUtils(script,env)
    }
    
    def downloadLatestRelease(String tag, String owner, String repo, String relname) {
        script.sh "gh release download ${tag} -R ${owner}/${repo} --pattern ${relname}"
    }
 
    def downloadAVHLatestRelease(String avh_tag, String avh_owner, String avh_repo, String avh_relname) {
        script.sh "gh release download ${avh_tag} -R ${avh_owner}/${avh_repo} --pattern ${avh_relname}"
    }

    def deleteFolderIfExists(String relname) {
        
        if (script.fileExists(relname)) {
            script.sh "rm -rf ${relname}"
        }
    }


    def deleteAVHFolderIfExists(String avh_relname) {
        
        if (script.fileExists(avh_relname)) {
            script.sh "rm -rf ${avh_relname}"
        }
    }
}
    
