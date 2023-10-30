package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.CommonNotificationStages



class CommonNotification extends BasePipeline {
    CommonNotificationStages commonNotificationStages
    
    Boolean skipPipeline = false

    CommonNotification(script, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: '''
                color = "blue"
                mimeType = ""
            ''',
            // the keys exposed to the user for modification
            exposed: [
                'webhookurl',
                'to',
                'from'
            ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        // Specify the node label expression
        // Looks like we can't use && syntax due to input parser
        nodeLabelExpr = "windows-lab-pc"

        commonNotificationStages = new CommonNotificationStages(script, env)
    }

    // /**
    // * Provides implementation for stages
    // *
    // * @param A Map with the inputs for stages
    // */
    @Override
    void stages(Map stageInputs) {
        logger.info("Random Custom stage")
        // Skip the entire pipeline if we promote and there are no changes
        if (skipPipeline) {
            return
        }
        logger.info("before post stage execution")
    
    }


    @Override
    void postParallel(Map stageInput) {
        if (skipPipeline) {
            return
        }

        script.stage("Post") {
            script.node(nodeLabelExpr) {
                //String from = stageInput.from
                //logger.info(from)
                logger.info("Notification:postPipeline")
                commonNotificationStages = new CommonNotificationStages(script, env)
                commonNotificationStages.stageNotificationTeams(env, stageInput)
                commonNotificationStages.stageNotificationEmail(env, stageInput)
            }
        }
    }

    
}