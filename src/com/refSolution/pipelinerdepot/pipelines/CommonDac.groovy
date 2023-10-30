package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.CommonDacStages



class CommonDac extends BasePipeline {
    CommonDacStages commonDacStages
    
    Boolean skipPipeline = false

    CommonDac(script, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: '''
                docsurl = https://github.com/karthickcob/Reference_solution.github.io.git
            ''',
            // the keys exposed to the user for modification
            exposed: [
                'doc_build', 
                'doc_publish',
                'docsurl' 
            ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        // Specify the node label expression
        // Looks like we can't use && syntax due to input parser
        nodeLabelExpr = "windows-lab-pc"

        commonDacStages = new CommonDacStages(script, env)
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
        commonDacStages.stageDacBuild(env, stageInput)
        commonDacStages.stageDacPublish(env, stageInput)
    }
}