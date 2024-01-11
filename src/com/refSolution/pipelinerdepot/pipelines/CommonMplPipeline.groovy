package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.CommonArtifactoryStages
import com.refSolution.pipelinerdepot.stages.CommonMplStages
import com.refSolution.pipelinerdepot.stages.QnxStages
import com.refSolution.pipelinerdepot.stages.CommonStages





class CommonMplPipeline extends BasePipeline {
      CommonArtifactoryStages commonArtifactoryStages
      CommonStages commonStages

 void execQnxPipeline(Map stageInput) {
        // Move the logic of execQnxPipeline here
        // You can use the 'script' object if needed
        // ...

        // Example:
        script.echo "Executing QnxPipeline with stageInput: ${stageInput}"
    }     
      
      

      Boolean skipPipeline = false

    CommonMplPipeline(script, Map defaults, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: """
               
               """
       + defaults.defaultInputs,
            // the keys exposed to the user for modification
            exposed:[

             ] + defaults.exposed,
             // the keys for which pipeline should be parallelized
            parallel: [] + defaults.parallel
        ] as Map, env, ioMap)

        commonArtifactoryStages = new CommonArtifactoryStages(script, env) 
        commonStages = new CommonStages(script, env)
        

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
        //execQnxPipeline(stageInput)
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

