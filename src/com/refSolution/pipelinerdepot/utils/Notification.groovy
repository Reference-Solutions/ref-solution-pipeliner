package com.refSolution.pipelinerdepot.utils

import com.bosch.pipeliner.LoggerDynamic
import com.bosch.pipeliner.ScriptUtils
import com.cloudbees.groovy.cps.NonCPS

import hudson.model.Job
import hudson.model.queue.QueueTaskFuture
import jenkins.model.ParameterizedJobMixIn

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.job.WorkflowRun

import org.slf4j.LoggerFactory
import org.slf4j.Logger


import hudson.model.Cause.UpstreamCause
import io.jenkins.plugins.coverage.CoverageAction
import io.jenkins.plugins.coverage.CoverageProjectAction
import io.jenkins.plugins.coverage.targets.CoverageElement
import io.jenkins.plugins.coverage.targets.Ratio
import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper
import groovy.json.*
import com.cloudbees.groovy.cps.NonCPS
import org.jfrog.hudson.pipeline.common.types.buildInfo.BuildInfo


/**
 * This class provides helper functions to improve Jenkins scripting
 */

public class Notification {


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
    public Notification(def script, def env) {
        this.script = script
        this.env = env
        this.logger = new LoggerDynamic(script)
        this.utils = new ScriptUtils(script,env)
    }
    


/*******************************
 EMAIL SPECIFIC FUNCTIONS
 
 LIBRARY: Send mail notification based on the given configuration
-------------------- Map config documentation ---------------------
     @param config = [body, from, subject, to]
-------------------------------------------------------------------
********************************/
	

def sendEmail(String from, String mimeType, String subject, String to) {
	script.emailext(	
		mimeType: 'text/html',
		subject: subject,
		body: '''${SCRIPT, template="managed:Groovy Email Template"}''',
		to: to
	)
}




def getBuildUser(){
    script.wrap([$class: 'BuildUser']) {
        return env.BUILD_USER_ID
    }
}



/*******************************
 TEAMS SPECIFIC FUNCTIONS
 
 STARTED -> blue ABORTED -> gray SUCCESS -> green FAILURE -> red UNSTABLE -> gray
 
 LIBRARY: Send teams notification based on the given configuration
-------------------- Map config documentation ---------------------
     @param config = [webhookUrl, message, status, color]
-------------------------------------------------------------------
 
********************************/
 
def sendTeams(String webhookUrl, String message, String status, String color){
	logger.info("sending teams notification")
	script.office365ConnectorSend webhookUrl: webhookUrl,
	message: message,
	status: status,
	color: getColorCode(color)
}
/**
 * Returns the hex color code for default colors being used in Teams notifications
  */
def getColorCode(String color) {
    Map colorMap = [:]
    colorMap['green'] = '00b100'
    colorMap['red'] = 'd00000'
    colorMap['blue'] = '1ccffd'
    colorMap['yellow'] = 'eae119'
    colorMap['gray'] = '808080'
    return colorMap[color]
}

/**
 * Returns the user ID triggered the job
*/





    
}