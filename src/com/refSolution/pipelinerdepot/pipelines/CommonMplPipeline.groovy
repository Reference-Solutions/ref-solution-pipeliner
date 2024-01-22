package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.CommonStages
import com.refSolution.pipelinerdepot.stages.CommonGitStages
import com.refSolution.pipelinerdepot.stages.ArcBswStages
import com.refSolution.pipelinerdepot.stages.QnxStages


class CommonMplPipeline extends BasePipeline {
    QnxStages qnxStages
    ArcBswStages arcBswStages
   

    Boolean skipPipeline = false

    CommonMplPipeline(script, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: """
                checkout_scm_stage = true
                checkout_stage = true
                build_stage = true
                versioning_stage = true
                archive_stage = true
                dac_stage = true
                artifactory_upload_stage = true
                sw_package_creation_stage = true
                label = windows-lab-pc
                artifact_version
                archive_patterns
            """,
            // the keys exposed to the user for modification
            exposed: [
                'checkout_scm_stage',
                'checkout_stage',
                'custom_scm_checkout_dir',
                'qnx_sdk_path',
              	'pfe_copy',
                'build_dir_path',
                'bsw_dir_path',
                'bsw_pre_build_file_name',
                'project_variant',
                'qnx_src_dir',
                'swc_dir_path',
                'gen_swp_dir_path',
                'app_dir_path',
                'app_name',
                'app_version',
                'action_type',
                'vrtefs_tool_path',
                'vpkg_dir_path'
            ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        
        qnxStages = new QnxStages(script, env)
        arcBswStages = new ArcBswStages(script, env)
        

    }

    // /**
    // * Provides implementation for stages
    // *
    // * @param A Map with the inputs for stages
    // */
    @Override
    void stages(Map stageInput) {
        // Skip the entire pipeline if we promote and there are no changes
        if (skipPipeline) {
            return
        }
        logger.info("stageInput")
        logger.info(stageInput.inspect())
       
        if (stageInput.qnx_build_stage == "true")
            qnxStages.stageBuild(env,stageInput)
        if (stageInput.m7_build_stage == "true")
            arcBswStages.stageBuild(env,stageInput)
        
    }
}