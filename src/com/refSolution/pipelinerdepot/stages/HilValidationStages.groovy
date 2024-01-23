//@Library('refsolution-rf-ci@master')
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.refSolution.pipelinerdepot.utils.Git


/**
* Contains stages that can be reused across pipelines
*/
class HilValidationStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private Git git
  

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    HilValidationStages(script, Map env) {
        this.script = script
        this.env = env
        this.git = new Git(script, env)
        this.logger = new LoggerDynamic(script)
    }

    def stageVerification(Map env, Map stageInput = [:]) {
        script.stage("Verification Validation") { 
            String robotframework_stage = stageInput.robotframework_stage?.trim() ?: "false"
            String pytest_stage = stageInput.pytest_stage?.trim() ?: "false"
            Map testConfig = [:]
            if (robotframework_stage == "true")
            {
                testConfig = prepareConfigForRobotTest(stageInput)
                executeTest(testConfig)
            }

            if (pytest_stage == "true")
            {
                testConfig = prepareConfigForPyTest(stageInput)
                executeTest(testConfig)
            }

            if (robotframework_stage != "true" && pytest_stage != "true") {
                logger.info "Skipped all the test stage"
            }   
        }
    }

    void prepareConfigForRobotTest(Map stageInput = [:]){
        Map config = [:]
        //checkout param configurations
        config["url"] = stageInput.robot_url?.trim() ?: "https://sourcecode.socialcoding.bosch.com/scm/~pow2kor/refsolution-rf-ci.git"
        config["branch"] = stageInput.robot_branch?.trim() ?: "feature/16550-HiL_Validation"
        config["credentialsId"] = stageInput.robot_checkout_credentials?.trim() ?: "Soco-credentials-hari"
        config["extensions"] = []

        String repoName = config["url"].replaceAll(".*/|.git","")
        config["extensions"] << [$class: 'RelativeTargetDirectory',
            relativeTargetDir: repoName]
        
        //Load Jenkinsfile configurations
        config["jenkinsfilePath"] = stageInput.robot_jenkins_file_path?.trim() ?: "refsolution-rf-ci/Jenkinsfile"

        return config
    }

    def executeTest(Map config = [:]){
        git.checkout(config.url ,config.branch ,config.credentialsId, config.extensions)
        logger.info("Git clone and checkout for test is successful.")

        script.load(config.jenkinsfilePath)
        logger.info("Test execution completed")
    }
}