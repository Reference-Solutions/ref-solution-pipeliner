package com.refSolution.pipelinerdepot.pipelines
import com.refSolution.pipelinerdepot.pipelines.CommonPipeline

import com.refSolution.pipelinerdepot.stages.CommonMplStages


class CommonMplPipeline extends CommonPipeline {
 
    
    Boolean skipPipeline = false

    CommonMplPipeline(script, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: '''
            vrtepull_stage = true
            qnx_stage = true
            opd-avh_stage = true
            qemu_stage = true
            flashing_stage = true
                
                
            ''',
            // the keys exposed to the user for modification
            exposed: [
                'vrtepull_stage',
                'qnx_stage' ,
                'opd-avh_stage',
                'qemu_stage',


              
                
            ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        // Specify the node label expression
        // Looks like we can't use && syntax due to input parser
        nodeLabelExpr = "windows-kiran"


    }

        
    // /**
    // * Provides implementation for stages
    // *
    // * @param A Map with the inputs for stages
    // */
    @Override
    void stages(Map stageInput) {
        
        logger.info("customStages")
        def customStages = getCustomStages()
        // Skip the entire pipeline if we promote and there are no changes
        if (skipPipeline) {
            return
        }
        logger.info("stageInput")
        logger.info(stageInput.inspect())
        if (stageInput.vrtepull_stage == "true")
            commonVrteStages.vrtePull(env, stageInput)
        if (stageInput.qnx_stage == "true")
            commonQnxStages.qnxBuild(env, stageInput)
        if (stageInput.opd-avh_stage == "true")
            commonOpdAvhStages.opdAVHApplications(env, stageInput) 
        if (stageInput.qemu_stage == "true")
            commonQemuStages.qemuValidation(env, stageInput)  
        if (stageInput.flashing_stage == "true")
            commonFlashStages.flashingM7(env, stageInput)     
                      
            

        
       
    }

    @Override
    void getCustomStages(){
        CommonMplStages customStages = new CommonMplStages(script, env)
    }
}

