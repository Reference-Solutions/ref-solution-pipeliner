package com.refSolution.pipelinerdepot.pipelines
import com.refSolution.pipelinerdepot.pipelines.CommonPipeline
import com.bosch.pipeliner.BasePipeline

import com.refSolution.pipelinerdepot.stages.CommonMplStages
import com.refSolution.pipelinerdepot.stages.QnxStages
// import com.refSolution.pipelinerdepot.stages.CommonVrteStages
// import com.refSolution.pipelinerdepot.stages.CommonQnxStage
// import com.refSolution.pipelinerdepot.stages.CommonOpdAvhStages
// import com.refSolution.pipelinerdepot.stages.CommonQemuStages
// import com.refSolution.pipelinerdepot.stages.CommonFlashStages 



class CommonMplPipeline extends BasePipeline {
      QnxStages qnxStages
 
    
    Boolean skipPipeline = false

    CommonMplPipeline(script, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: '''
            qnx_stage = true
                
                
            ''',
            // the keys exposed to the user for modification
            exposed: [
               'qnx_stage',
               'qnx_sdk_path',
               'custom_scm_checkout_dir',
               'pfe_copy'


              
                
            ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        // Specify the node label expression
        // Looks like we can't use && syntax due to input parser
        nodeLabelExpr = "windows-kiran"

    //  commonVrteStages = new  CommonVrteStages(script, env)
        qnxStages = new QnxStages(script, env)
    //  commonOpdAvhStages = new CommonOpdAvhStages(script, env)
    //  commonQemuStages = new CommonQemuStages (script, env)
    //  commonFlashStages = new CommonFlashStages (script, env)





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
        //if (stageInput.vrtepull_stage == "true")
            //commonVrteStages.vrtePull(env, stageInput)
        if (stageInput.qnx_stage == "true")
           qnxStages.stageBuild(env, stageInput)
           qnxStages.makeBuild(env, stageInput)
           qnxStages.copyPFE(env, stageInput)
        //if (stageInput.opd-avh_stage == "true")
            //commonOpdAvhStages.opdAVHApplications(env, stageInput) 
        //if (stageInput.qemu_stage == "true")
            //commonQemuStages.qemuValidation(env, stageInput)  
        //if (stageInput.flashing_stage == "true")
            //commonFlashStages.flashingM7(env, stageInput)     
                      
            

        
       
    }

    void getCustomStages(){
        CommonMplStages customStages = new CommonMplStages(script, env)
    }
}

