
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.refSolution.pipelinerdepot.utils.Sonar

/**
* Contains stages that can be reused across pipelines
*/
class CommonSonarStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private Sonar sonar

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    CommonSonarStages(script, Map env) {
        this.script = script
        this.env = env
        this.sonar = new Sonar(script, env)
        this.logger = new LoggerDynamic(script)
    }

    def stageSonarAnalysis(Map env, Map stageInput = [:]){
        script.stage("Sonar Analysis") { 
            logger.info('SONAR ANALYSIS STAGE')
            String sonarPropertyFilePath = stageInput.sonarPropertyFilePath?.trim() ?: 'sonar.properties'
            logger.info("sonarPropertyFilePath : " + sonarPropertyFilePath)
            sonar.sonarAnalysis(sonarPropertyFilePath)
        }
    }
}
