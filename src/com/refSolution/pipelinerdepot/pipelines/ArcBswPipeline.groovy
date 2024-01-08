package com.refSolution.pipelinerdepot.pipelines

import com.refSolution.pipelinerdepot.pipelines.CommonPipeline
import com.refSolution.pipelinerdepot.stages.CommonStages
import com.refSolution.pipelinerdepot.stages.ArcBswStages


class ArcBswPipeline extends CommonPipeline {
    CommonStages commonStages
    
    Boolean skipPipeline = false

    ArcBswPipeline(script, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: '''
                archive_patterns
                build_dir_path
                bsw_dir_path
                asw_build_dir_path
                bsw_build_dir_path
                integeration_dir_path
                bsw_pre_build_file_name
                asw_build_file_name
                bsw_build_file_name
                asw_build
                bsw_build
                integeration_file_name
                autosar_tool
                autosar_tool_version
                autosar_tool_env
                project_variant
            ''',
            // the keys exposed to the user for modification
            exposed: [
                'archive_patterns',
                'build_dir_path',
                'bsw_dir_path',
                'asw_build_dir_path',
                'bsw_build_dir_path',
                'integeration_dir_path',
                'bsw_pre_build_file_name',
                'asw_build_file_name',
                'bsw_build_file_name',
                'asw_build',
                'bsw_build',
                'integeration_file_name',
                'autosar_tool',
                'autosar_tool_version',
                'autosar_tool_env',
                'project_variant'
            ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        // Specify the node label expression
        // Looks like we can't use && syntax due to input parser
        nodeLabelExpr = "windows-lab-pc"

        
        commonStages = new CommonStages(script, env)
    }

    @Override
    void getCustomStages(){
        ArcBswStages customStages = new ArcBswStages(script, env)
    }
}