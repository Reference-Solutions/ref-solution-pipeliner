
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.refSolution.pipelinerdepot.utils.Notification

/**
* Contains stages that can be reused across pipelines
*/
class CommonNotificationStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private Notification notification

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    CommonNotificationStages(script, Map env) {
        this.script = script
        this.env = env
        this.notification = new Notification(script, env)
        this.logger = new LoggerDynamic(script)
    }

    def stageNotificationEmail(Map env, Map stageInput = [:]){
        script.stage("Email Notification") { 
            logger.info('Email Notification')
            String from = stageInput.from
            String mimeType = stageInput.mimeType
            String subject = "Build ${script.currentBuild.currentResult} in Jenkins: ${env.JOB_NAME} #${env.BUILD_NUMBER} - TRIGGERED BY: ${getBuildUser()}"
            String to = stageInput.to
            notification.sendEmail(from, mimeType, subject, to)
            
        }
    }

    def stageNotificationTeams(Map env, Map stageInput = [:]){
        script.stage("Teams Notification") { 
            logger.info("before starting teams")
            String webhookUrl = stageInput.webhookurl
            String message =  "Build ${env.BUILD_DISPLAY_NAME}<br>Duration: ${script.currentBuild.durationString}<br>Node:${script.env.NODE_NAME} <br>TRIGGERED BY: ${getBuildUser()}<br>Build Url : &QUOT;<a href='${env.BUILD_URL}/console'>${env.JOB_NAME} #${env.BUILD_NUMBER}</a>"
            String status = script.currentBuild.currentResult
            String color = stageInput.color
            logger.info(webhookUrl)
            logger.info(message)
            logger.info(status)
            if (status == "SUCCESS")
		        color = "green"
	        else if (status == "FAILURE")
		        color = "red"
	        else if (status == "UNSTABLE")
		        color = "gray"
            notification.sendTeams(webhookUrl, message, status, color)
            logger.info('Teams Notification')
            
        }
    }

//     def getBuildUser(){
//     script.wrap([$class: 'BuildUser']) {
//         return env.BUILD_USER_ID
//     }
// }
    def getBuildUser() {
	if(script.currentBuild.rawBuild.getCause(Cause.UserIdCause) != null)
		return script.currentBuild.rawBuild.getCause(Cause.UserIdCause).getUserId()
	else
		return "Timer"
}
}
