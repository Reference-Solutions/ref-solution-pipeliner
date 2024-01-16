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
                
                
            ''',
            // the keys exposed to the user for modification
            exposed: [
              
                
            ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        // Specify the node label expression
        // Looks like we can't use && syntax due to input parser
        nodeLabelExpr = "windows-kiran"

        
       
    }

    @Override
    void getCustomStages(){
        CommonMplStages customStages = new CommonMplStages(script, env)
    }
}

