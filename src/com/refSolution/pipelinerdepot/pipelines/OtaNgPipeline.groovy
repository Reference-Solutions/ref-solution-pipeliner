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
            """ + defaults.defaultInputs,
            // the keys exposed to the user for modification
            exposed: [
                'checkout_scm_stage',
                'swc_dir_path',
                'gen_swp_dir_path',
                'app_dir_path',
                'app_name',
                'app_version',
                'action_type',
                'vrtefs_tool_path',
                'vpkg_dir_path',
                'github_releaseTag',
                'github_owner',
                'github_repo',
                'github_pattern',

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
        customStages.stageDownloadApplication(env, stageInput)
        customStages.stageSwPackgeCreation(env, stageInput)
        customStages.stageVehicePackgeCreation(env, stageInput)

    }
    
    void getCustomStages(){
        OtaNgStages customStages = new OtaNgStages(script, env)
    }
}