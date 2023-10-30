package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.CommonArtifactoryStages



class CommonArtifactory extends BasePipeline {
    CommonArtifactoryStages commonArtifactoryStages
    
    Boolean skipPipeline = false

    CommonArtifactory(script, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: '''
                artifactory_target = target
            ''',
            // the keys exposed to the user for modification
            exposed: [
                'artifactory_target',
                'artifactory_pattern'
            ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        // Specify the node label expression
        // Looks like we can't use && syntax due to input parser
        nodeLabelExpr = "windows-lab-pc"

        commonArtifactoryStages = new CommonArtifactoryStages(script, env)
    }

    // /**
    // * Provides implementation for stages
    // *
    // * @param A Map with the inputs for stages
    // */
    @Override
    void stages(Map stageInput) {
        logger.info("customStages")
        // Skip the entire pipeline if we promote and there are no changes
        if (skipPipeline) {
            return
        }
        logger.info("stageInput")
        logger.info(stageInput.inspect())
        commonArtifactoryStages.stageArtifactoryDownload(env, stageInput)
    }
}