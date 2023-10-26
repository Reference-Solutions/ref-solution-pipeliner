
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.refSolution.pipelinerdepot.utils.Artifactory

/**
* Contains stages that can be reused across pipelines
*/
class CommonArtifactoryStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private Artifactory artifactory

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    CommonArtifactoryStages(script, Map env) {
        this.script = script
        this.env = env
        this.artifactory = new Artifactory(script, env)
        this.logger = new LoggerDynamic(script)
    }

    def stageArtifactoryDownload(Map env, Map stageInput = [:]){
        script.stage("Artifactory Downlaod") { 
            logger.info('DOWNLOAD FROM ARTIFACTORY STAGE')
            String pattern = stageInput.artifactory_pattern.trim()
            String target = stageInput.artifactory_target.trim()
            artifactory.downloadFromArtifactory(pattern, target)
        }
    }

    def stageArtifactoryUpload(Map env, Map stageInput = [:]){
        script.stage("Artifactory Downlaod") { 
            logger.info('DOWNLOAD FROM ARTIFACTORY STAGE')
            String pattern = stageInput.artifactory_upload_pattern.trim()
            String target = stageInput.artifactory_upload_target.trim()
            artifactory.uploadToArtifactory(pattern, target)
        }
    }
}
