package com.refSolution.pipelinerdepot.pipelines
import com.refSolution.pipelinerdepot.pipelines.CommonPipeline
import com.bosch.pipeliner.BasePipeline

import com.refSolution.pipelinerdepot.stages.CommonMplStages
import com.refSolution.pipelinerdepot.stages.CommonStages





class CommonMplPipeline extends BasePipeline {
      CommonStages commonStages
      
 
    
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
               'archive_patterns',
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
        nodeLabelExpr = "windows-lab-pc"

   
        commonStages = new CommonStages(script, env)
        
    

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
              commonStages.stageBuild(env, stageInput)
        //    commonStages.makeBuild(env, stageInput)
          
              commonStages.copyPFE(env, stageInput)
           
        
                      
            

        
       
    }


    void getCustomStages(){
        CommonMplStages customStages = new CommonMplStages(script, env)
    }
}

