package com.refSolution.pipelinerdepot.pipelines

import com.refSolution.pipelinerdepot.pipelines.CommonPipeline
import com.refSolution.pipelinerdepot.stages.CommonStages
import com.refSolution.pipelinerdepot.stages.QnxStages


class QnxPipeline extends CommonPipeline {
    QnxStages qnxStages
    
    Boolean skipPipeline = false

    QnxPipeline(script, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: '''
                archive_patterns = qnx-hv-nxp-s32g/images/*.ui
                custom_scm_checkout_dir = qnx-hv-nxp-s32g
                qnx_sdk_path = C:/Users/zrd2kor/qnx710
            ''',
            // the keys exposed to the user for modification
            exposed: [
                'archive_patterns',
                'custom_scm_checkout_dir',
                'qnx_sdk_path',
              	'pfe_copy'
            ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        // Specify the node label expression
        // Looks like we can't use && syntax due to input parser
        nodeLabelExpr = "windows-lab-pc"

        
        qnxStages = new QnxStages(script, env)
    }

    @Override
    void getCustomStages(){
        QnxStages customStages = new QnxStages(script, env)
    }
}