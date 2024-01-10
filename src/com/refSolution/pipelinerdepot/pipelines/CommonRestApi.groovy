package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.CommonRestApiStages



class CommonRestApi extends BasePipeline {
    CommonRestApiStages commonRestApiStages
    
    Boolean skipPipeline = false

    CommonRestApi(script, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: '''
                accessTokenUrl = https://p2.authz.bosch.com/auth/realms/EU_RB_FLEATEST/protocol/openid-connect/token
                proxiesValue = ['http': 'http://rb-proxy-in.bosch.com:8080', 'https': 'http://rb-proxy-in.bosch.com:8080']
                client_id = ['tech-client-03']
                client_secret = ['MMTjYq7Prp2vIETEHYZ4eG6bOUIXIOBD']
            ''',
            // the keys exposed to the user for modification
            exposed: [
                'accessTokenUrl', 
                'proxiesValue',
                'client_id',
                'client_secret',
                'accessToken' 
            ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        // Specify the node label expression
        // Looks like we can't use && syntax due to input parser
        nodeLabelExpr = "Karthick"

        commonRestApiStages = new CommonRestApiStages(script, env)
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
        commonRestApiStages.getDesirtedStateByName(env, stageInput)
        //commonRestApiStages.method2(env, stageInput)
    }
}