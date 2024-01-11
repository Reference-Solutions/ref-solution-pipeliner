package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.CommonArtifactoryStages
import com.refSolution.pipelinerdepot.stages.CommonMplStages




class CommonMplPipeline extends BasePipeline {
      CommonArtifactoryStages commonArtifactoryStages
      
      

      Boolean skipPipeline = false

    CommonMplPipeline(script, Map defaults, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: """
               artifactory_stage = true
               artifactory_pattern = true
               artifactory_target = true
               """
       + defaults.defaultInputs,
            // the keys exposed to the user for modification
            exposed:['artifactory_stage',
                'target',
                'artifactory_pattern',
                'artifactory_target',
                'pattern'
               ] + defaults.exposed,
             // the keys for which pipeline should be parallelized
            parallel: [] + defaults.parallel
        ] as Map, env, ioMap)

        commonArtifactoryStages = new CommonArtifactoryStages(script, env) 
        

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
        script.stage("VRTE pull") 
        script.stage("QNX+BSW build and relaese")
        // Call execQnxPipeline script
        execQnxPipeline(stageInput)
        script.stage("OPD/AVH Applications")
        script.stage("QEMU Validation")
        script.stage("Flashing M7 Software Application")
        script.stage("Validation of HiL")
        script.stage("Create S/W Package Artifacts")
        script.stage("Create Vehicle Package Artifacts")

        

        


    }




  

    void getCustomStages(){
        CommonMplStages customStages = new CommonMplStages(script, env)

        
    }   

}

