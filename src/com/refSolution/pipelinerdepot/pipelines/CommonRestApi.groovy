package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.CommonRestApiStages
import com.refSolution.pipelinerdepot.stages.CommonGitStages


class CommonRestApi extends BasePipeline {
    CommonRestApiStages commonRestApiStages
    CommonGitStages commonGitStages

    Boolean skipPipeline = false

    CommonRestApi(script, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: """
		        listAllVehicleDetails = true
                desired_vehicle_id = "Id not provided"
                create_blob_and_desiredstate = false
                verify_device_with_Id = true
                verify_blob_with_Id = true
                checkout_scm_stage = true
            """,
            // the keys exposed to the user for modification
            exposed: [
		        'listAllVehicleDetails',
                'create_blob_and_desiredstate', 
                'verify_device_with_Id',
                'verify_blob_with_Id',
                'blob_Id',
                'device_Id',
                'checkout_scm_stage' 
                'desired_vehicle_id'
            ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        // Specify the node label expression
        // Looks like we can't use && syntax due to input parser
        nodeLabelExpr = "windows-pooja"

        commonRestApiStages = new CommonRestApiStages(script, env)
        commonGitStages = new CommonGitStages(script, env)
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
        if (stageInput.checkout_scm_stage == "true")
            commonGitStages.stageCheckoutSCM(env, stageInput)
            commonRestApiStages.verifyblob(env, stageInput)
            commonRestApiStages.verifyDeviceStatus(env, stageInput)
            
        if (stageInput.verify_device_with_Id == "true")    
            commonRestApiStages.verifyDeviceStatus(env, stageInput)
        if (stageInput.verfy_blob_with_Id == "true")
            commonRestApiStages.verifyblob(env, stageInput)
        if (stageInput.create_blob_and_desiredstate == "true")
            commonRestApiStages.createBlobAndDesiredState(env, stageInput)
	    
        if (stageInput.listAllVehicleDetails=='true')
        commonRestApiStages.listAllVehicleDetails(env,stageInput)


    }
}