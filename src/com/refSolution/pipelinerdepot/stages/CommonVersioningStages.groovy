
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.refSolution.pipelinerdepot.utils.Versioning

/**
* Contains stages that can be reused across pipelines
*/
class CommonVersioningStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private Versioning versioning

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    CommonVersioningStages(script, Map env) {
        this.script = script
        this.env = env
        this.versioning = new Versioning(script, env)
        this.logger = new LoggerDynamic(script)
    }

    def stageVersioningArtifacts(Map env, Map stageInput = [:]) {
        script.stage("Versioning") {
            if (stageInput.containsKey("archive_patterns") && stageInput.containsKey("build_version")){
                ArrayList<String> patterns = stageInput.archive_patterns.split(" ")
                String artifactVersion = stageInput.build_version
                String buildEnvironment = stageInput.build_env?.trim() ?: "Nightly"
                String buildDate = versioning.buildDate()
                if (buildEnvironment == "Nightly"){
                    artifactVersion = artifactVersion + "_" + buildDate + "_N"
                }
                else if (buildEnvironment == "Dev"){
                    artifactVersion = artifactVersion + "_" + buildDate + "_Dev"
                }
                else if (buildEnvironment == "Release"){
                    artifactVersion = artifactVersion
                }

                logger.info("Artifact Version : " + artifactVersion)

                for (pattern in patterns) {
                    versioning.versioningArtifact(pattern, artifactVersion)
                }
                versioning.updateBuildDisplay(artifactVersion)
            }
            else{
                logger.info('Artifacts Pattern or Artifacts Version Not Provided..., So Versioning Artifacts has been skipped')
            }
        }
    }
}
