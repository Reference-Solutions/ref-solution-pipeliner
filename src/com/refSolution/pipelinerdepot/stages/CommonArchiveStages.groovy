
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic

/**
* Contains stages that can be reused across pipelines
*/
class CommonArchiveStages {

    /**
     * for shallow clone using gitlab_branch_source plugin, the minimum depth
     * has to be at least the number of commits in the MR + 1, so the latest
     * commit from main branch is also fetched, and the MR can be merged into it.
     * in case the value is not sets, perform a full clone
     */

    private def script
    private Map env
    private LoggerDynamic logger

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    CommonArchiveStages(script, Map env) {
        this.script = script
        this.env = env
        this.logger = new LoggerDynamic(script)
    }

    /**
     * Captures the files built matching the included pattern and saves them to the
     * Jenkins master as build artifacts.
     *
     * @param patterns List of pattern strings that should be saved
     */
    def stageArchive(Map stageInput = [:]) {
        script.stage("Archive") {
            if (stageInput.containsKey("archive_patterns")){
                ArrayList<String> patterns = stageInput.archive_patterns.split(" ")
                for (pattern in patterns) {
                    script.archiveArtifacts pattern
                }
            }
            else{
                logger.info('No Artifacts Pattern Provided...')
            }
        }
    }
}
