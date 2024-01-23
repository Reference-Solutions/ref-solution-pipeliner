package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.HilValidationStages


class HilValidation extends BasePipeline {
    HilValidationStages hilValidationStages
    
    Boolean skipPipeline = false
   
    HilValidation(script, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
             defaultInputs: '''
                robot_manifest_file_path = refsolution-rf-ci/manifest.xml
                robot_url = https://sourcecode.socialcoding.bosch.com/scm/~pow2kor/refsolution-rf-ci.git
                robot_branch = feature/16550-HiL_Validation
                robot_checkout_credentials = Soco-credentials-hari
                robotframework_stage = false
                robot_jenkins_file_path = refsolution-rf-ci/Jenkinsfile
            ''',
            // the keys exposed to the user for modification
            exposed: [
                'robot_manifest_file_path',
                'robotframework_stage'
            ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        // Specify the node label expression
        // Looks like we can't use && syntax due to input parser
        nodeLabelExpr = "sree-apertis"

        hilValidationStages = new HilValidationStages(script, env)
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
        hilValidationStages.stageVerification(env, stageInput)
    }
}