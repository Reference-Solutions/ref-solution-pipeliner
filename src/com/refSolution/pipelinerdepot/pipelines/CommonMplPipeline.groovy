package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.CommonArtifactoryStages
//import com.refSolution.pipelinerdepot.stages.CommonMplStages
import com.refSolution.pipelinerdepot.stages.CommonQnxStages



class CommonMplPipeline extends BasePipeline {
      //CommonArtifactoryStages commonArtifactoryStages
      CommonQnxStages commonQnxStages
      

      Boolean skipPipeline = false

    CommonMplPipeline(script, Map defaults, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: """
               artifactory_stage = true
               artifactory_pattern = true
               artifactory_target = true

               label = windows-lab-pc

            """+ defaults.defaultInputs,
            // the keys exposed to the user for modification
            exposed:['artifactory_stage',
                'target',
                
                'pattern'
                
                ] + defaults.exposed,
             // the keys for which pipeline should be parallelized
            parallel: [] + defaults.parallel
        ] as Map, env, ioMap)

        //commonArtifactoryStages = new CommonArtifactoryStages(script, env)
        commonQnxStages = new CommonQnxStages(script ,env)
    }

     // /**
    // * Provides implementation for stages
    // *
    // * @param A Map with the inputs for stages
    // */
    @Override
    void stages(Map stageInput) {
        
        logger.info("customStages")
        def customStages = getCustomStages()
        // Skip the entire pipeline if we promote and there are no changes
        if (skipPipeline) {
            return
        }
        logger.info("stageInput")
        logger.info(stageInput.inspect())

            
            if (stageInput.artifactory_stage  == "true")
            commonArtifactoryStages.stageArtifactoryDownload(env, stageInput)
            commonArtifactoryStages.stageArtifactoryUpload(env, stageInput)   

              }

    void getCustomStages(){
        CommonMplStages customStages = new CommonMplStages(script, env)
    }   

}

