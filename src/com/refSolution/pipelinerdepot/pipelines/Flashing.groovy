package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.FlashingStages


class Flashing extends BasePipeline {
    FlashingStages flashingStages
    
    Boolean skipPipeline = false

    Flashing(script, Map env, Map ioMap, String nodeLabelExpr = "windows-pc") {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: '''
                arcbsw_binary_dir = C:/FlashScriptsS32G2/Final_Auto/binary/vip_int.bin
                qnx_binary_dir = C:/FlashScriptsS32G2/Final_Auto/binary/ifs-s32g-vip.ui
                custom_scm_checkout_dir = vip_flashing
            ''',
            // the keys exposed to the user for modification
            exposed: [
                'arcbsw_binary_dir',
                'custom_scm_checkout_dir',
                'qnx_binary_dir'
            ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        // Specify the node label expression
        // Looks like we can't use && syntax due to input parser
        //nodeLabelExpr = "sdv-lab"
        this.nodeLabelExpr = nodeLabelExpr

        
        flashingStages = new FlashingStages(script, env)
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
        flashingStages.stageVerifyT32(env, stageInput)
        flashingStages.stageFlashing(env, stageInput)
    }
}
