package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.QemuStages
import com.refSolution.pipelinerdepot.stages.CommonGitStages



class CommonQemu extends BasePipeline {
    QemuStages qemuS
    CommonGitStages commonGitStages
    
    Boolean skipPipeline = false

    CommonQemu(script, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: '''
                vrte_qemu_dir = ""
                robot_test_dir = ""
                repo_name = ""
	            robot_options = ""

            ''',
            // the keys exposed to the user for modification
            exposed: [
                'vrte_qemu_dir',
                'robot_options',
                'robot_test_dir',
                'vrte_arch_type'
            ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        // Specify the node label expression
        // Looks like we can't use && syntax due to input parser
        nodeLabelExpr = "Karthick"

        qemuS = new QemuStages(script, env)
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
        commonGitStages.stageCheckoutSCM(env, stageInput)
        commonGitStages.stageCheckout(env, stageInput)
        qemuS.StageLoadVRTE(env, stageInput)
        qemuS.StageTest(env, stageInput)

    }

    @Override
    void postParallel(Map stageInput) {
        if (skipPipeline) {
            return
        }

        logger.info("publishHTMLTestReport")
        qemuS = new QemuStages(script, env)
        qemuS.stagePublishReport(env, stageInput)
                
            
        }
    }
    
