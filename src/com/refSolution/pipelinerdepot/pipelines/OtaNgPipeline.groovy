package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.OtaNgStages
import com.refSolution.pipelinerdepot.stages.CommonGitStages


class OtaNgPipeline extends BasePipeline {
    CommonGitStages commonGitStages

    
    Boolean skipPipeline = false

    OtaNgPipeline(script, Map defaults, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: """
                checkout_scm_stage = true
                app_pull_stage = true
                sw_package_creation_stage = true
                desired_state_creation_stage = true
                device_creation_stage = true
                vehicle_creation_stage = true
            """ + defaults.defaultInputs,
            // the keys exposed to the user for modification
            exposed: [
                'checkout_scm_stage',
                'app_pull_stage',
                'sw_package_creation_stage',
                'desired_state_creation_stage',
                'device_creation_stage',
                'vehicle_creation_stage',
                'gh_release_tag',
                'gh_owner',
                'gh_repo',
                'gh_pattern',
                'app_zip_sub_folder',
                'swc_dir_path',
                'gen_swp_dir_path',
                'app_dir_path',
                'app_name',
                'app_version',
                'action_type',
                'vrtefs_tool_path',
                'vpkg_dir_path',
                'pantaris_node',
                'pantaris_script_path',
                'vehicle_id',
                'device_id'

            ] + defaults.exposed,
            // the keys for which pipeline should be parallelized
            parallel: [] + defaults.parallel
        ] as Map, env, ioMap)

        commonGitStages = new CommonGitStages(script, env)

    }

    // /**
    // * Provides implementation for stages
    // *
    // * @param A Map with the inputs for stages
    // */
    @Override
    void stages(Map stageInput) {
        
        def customStages = getCustomStages()

        // Skip the entire pipeline if we promote and there are no changes
        if (skipPipeline) {
            return
        }
        logger.info("stageInput")
        logger.info(stageInput.inspect())
        if (stageInput.checkout_scm_stage == "true")
            commonGitStages.stageCheckoutSCM(env, stageInput)
        if (stageInput.app_pull_stage == "true"){
            customStages.stagePullAppFromGithub(env, stageInput)
        }
        if (stageInput.sw_package_creation_stage == "true"){
            customStages.stageSwPackgeCreation(env, stageInput)
            customStages.stageVehicePackgeCreation(env, stageInput)
        }
        if (stageInput.desired_state_creation_stage == "true"){
            customStages.stageDesiredStateCreation(env, stageInput)
        }
        if (stageInput.device_creation_stage == "true"){
            customStages.stageDeviceCreation(env, stageInput)
        }
        if (stageInput.vehicle_creation_stage == "true"){
            customStages.stageVehicleCreation(env, stageInput)
        }
    }
    
    void getCustomStages(){
        OtaNgStages customStages = new OtaNgStages(script, env)
    }
}