package com.refSolution.pipelinerdepot.utils

import com.bosch.pipeliner.LoggerDynamic
import com.cloudbees.groovy.cps.NonCPS

/**
 * This class provides helper functions to improve Jenkins scripting
 *

 */

public class Git {
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
    public Git(def script, def env) {
        this.script = script
        this.logger = new LoggerDynamic(script)
    }


    /**
     *
     * Checks out the associated git repository using GitSCM step
     *
     * @param url String of checkout URL
     * @param branch String of checkout branch
     * @param credentialsId String of checkout credentials identifier
     * @param extensions ArrayList of optional extensions for GitSCM checkout
     * @see <a href=https://www.jenkins.io/doc/pipeline/steps/workflow-scm-step/>Workflow SCM</a>
     */
    private void doCheckout(String url=null, String branch=null, String credentialsId=null, ArrayList extensions=[]) {
        boolean customCheckout = url != null
        
        // scm attributes are non-serializable objects, so the logic to set attributes must be within the checkout step
        script.checkout([
            $class: 'GitSCM',
            branches: customCheckout ? [[name: '*/' + branch]] : script.scm.branches,
            extensions: (customCheckout || !script.scm.extensions) ? extensions : script.scm.extensions + extensions,
            userRemoteConfigs: customCheckout ? [[url: url, credentialsId: credentialsId]] : script.scm.userRemoteConfigs
        ])
    }

    /**
     *
     * Checks out repository with given arguments
     *
     * @param url String of checkout URL
     * @param branch String of checkout branch
     * @param credentialsId String of checkout credentials identifier
     * @param extensions ArrayList of optional extensions for GitSCM checkout
     * @see <a href=https://www.jenkins.io/doc/pipeline/steps/workflow-scm-step/>Workflow SCM</a>
     */
    def checkout(String url=null, String branch=null, String credentialsId=null, ArrayList extensions=[]) {
        if (url) {
            logger.info("Running parameterized checkout")
            logger.info("Url: " + url)
            logger.info("Branch: " + branch)
        } else {
            logger.info("Running standard checkout")
        }
        doCheckout(url, branch, credentialsId, extensions)

        logger.info("Checkout complete")
    }
}