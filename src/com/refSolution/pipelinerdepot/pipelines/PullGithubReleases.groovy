package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.PullAppStages



class PullGithubReleases extends BasePipeline {
    PullAppStages pullAppStages
    
    Boolean skipPipeline = false

    PullGithubReleases(script, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: '',
            // the keys exposed to the user for modification
            exposed: [
                'app_release_tag',
                'app_owner',
                'app_repo',
                'app_pattern',
                'app_name',
                'app_path',
                'app_folder'

            ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        // Specify the node label expression
        // Looks like we can't use && syntax due to input parser
        nodeLabelExpr = "windows-lab-pc"

        pullAppStages = new PullAppStages(script, env)
    }

    // /**
    // * Provides implementation for stages
    // *
    // * @param A Map with the inputs for stages
    // */
    @Override
    void stages(Map stageInput) {
        // Skip the entire pipeline if we promote and there are no changes
        if (skipPipeline) {
            return
        }
        logger.info("stageInput")
        logger.info(stageInput.inspect())
        pullAppStages.stagePullAppFromGithub(env, stageInput)
    }
}