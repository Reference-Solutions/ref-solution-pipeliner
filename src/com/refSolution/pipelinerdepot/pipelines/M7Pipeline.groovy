package com.refSolution.pipelinerdepot.pipelines

import com.refSolution.pipelinerdepot.pipelines.CommonPipeline
import com.refSolution.pipelinerdepot.stages.CommonStages
import com.refSolution.pipelinerdepot.stages.M7Stages


class M7Pipeline extends CommonPipeline {
    CommonStages commonStages
    
    Boolean skipPipeline = false

    M7Pipeline(script, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: '''
                archive_patterns = vip-project-uC/out/variants/vip-mainboard/s32g-uC/arc/bin/
                build_repo = vip-project-uC
                bsw_repo = cubas-nxp-s32g
                autosar_tool = aeee_pro
                autosar_tool_version = 2021.2.0.3
                autosar_tool_env = cdg.de
                project_variant = VIP_MAIN_BOARD
            ''',
            // the keys exposed to the user for modification
            exposed: [
                'archive_patterns',
                'build_repo',
                'bsw_repo',
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

    
    
    void getCustomStages(){
        M7Stages customStages = new M7Stages(script, env)
    }
}