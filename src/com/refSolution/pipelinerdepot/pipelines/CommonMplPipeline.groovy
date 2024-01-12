package com.refSolution.pipelinerdepot.pipelines

import com.refSolution.pipelinerdepot.pipelines.CommonPipeline
import com.refSolution.pipelinerdepot.stages.CommonStages
import com.refSolution.pipelinerdepot.stages.CommonMplStages


class CommonMplPipeline extends CommonPipeline {
    CommonStages commonStages
    
    Boolean skipPipeline = false

    CommonMplPipeline(script, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: '''
                archive_patterns = qnx-hv-nxp-s32g/images/*.ui
                custom_scm_checkout_dir = qnx-hv-nxp-s32g
                qnx_sdk_path = C:/Users/zrd2kor/qnx710
                arcbsw_binary_dir = C:/FlashScriptsS32G2/Final_Auto/binary/vip_int.bin
                qnx_binary_dir = C:/FlashScriptsS32G2/Final_Auto/binary/ifs-s32g-vip.ui
                custom_scm_checkout_dir = vip_flashing
            ''',
            // the keys exposed to the user for modification
            exposed: [
                'archive_patterns',
                'custom_scm_checkout_dir',
                'qnx_sdk_path',
              	'pfe_copy',
                'arcbsw_binary_dir',
                'custom_scm_checkout_dir',
                'qnx_binary_dir'
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
        CommonMplStages customStages = new CommonMplStages(script, env)
    }
}

