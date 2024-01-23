
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.refSolution.pipelinerdepot.utils.GhCli

/**
* Contains stages that can be reused across pipelines
*/
class PullAppStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private GhCli ghCli

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    PullAppStages(script, Map env) {
        this.script = script
        this.env = env
        this.ghCli = new GhCli(script, env)
        this.logger = new LoggerDynamic(script)
    }

    def stagePullAppFromGithub(Map env, Map stageInput = [:]){
        String releaseTag = stageInput.app_release_tag?.trim()
        String owner = stageInput.app_owner?.trim()
        String repo = stageInput.app_repo?.trim()
        String pattern = stageInput.app_pattern?.trim()
        String appName = stageInput.app_name?.trim()
        String appPath = stageInput.app_path?.trim()
        String appFolder = stageInput.app_folder?.trim()
        String patToken = stageInput.pat_token?.trim()

        script.stage("Pull Application") { 
            ghCli.pullArtifactfromRelease( releaseTag, owner, repo, pattern, appName, appPath, appFolder, patToken)
        }
    }
}
