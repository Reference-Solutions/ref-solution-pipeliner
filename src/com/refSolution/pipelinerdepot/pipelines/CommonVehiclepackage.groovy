package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.CommonVehicleStages



class CommonVehiclepackage extends BasePipeline {
    CommonVehicleStages commonVehicleStages
    
    Boolean skipPipeline = false

     CommonVehiclepackage(script, Map env, Map ioMap, String nodeLabelExprParam = "windows-lab-pc") {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: '''
                vec_package_dir = C:/Vehcile_package/vehicle_package_manifest.json
            ''',
            // the keys exposed to the user for modification
            exposed: [
                'vec_package_dir',

               ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        // Specify the node label expression
        // Looks like we can't use && syntax due to input parser
        //nodeLabelExpr = "windows-lab-pc"
        // Set the nodeLabelExpr using the parameter value
        nodeLabelExpr = nodeLabelExprParam

        
        commonVehicleStages = new CommonVehicleStages(script, env)
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
        commonVehicleStages.stageVehiclepackage(env, stageInput)
        
    }
}   
                