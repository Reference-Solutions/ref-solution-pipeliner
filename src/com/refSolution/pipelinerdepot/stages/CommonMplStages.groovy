package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.bosch.pipeliner.ScriptUtils



/**
* Contains stages that can be reused across pipelines
*/
class CommonMplStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private ScriptUtils utils
    private CommonStages commonStages
   

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    CommonMplStages(script, Map env) {
        this.script = script
        this.env = env
        this.logger = new LoggerDynamic(script)
        this.utils = new ScriptUtils(script, env)
        this.commonStages = new CommonStages(script, env)
       
    }

    // def vrtePull(Map env, Map stageInput = [:]) {
    //     // add vrte pull stages here
    //     script.echo "Vrte pull"
    // }

     def stageBuild(Map env, Map stageInput = [:]){
         qnxStages.stageBuild(env, [:])

     }
    
    // def opdAVHApplications(Map env, Map stageInput = [:]) {
    //     // add OPD/AVH Applications stages here
    //     script.echo "OPD/AVH Applications"
    // }


    // def qemuValidation(Map env, Map stageInput = [:]) {
    //     // add QEMU Validation stages here
    //     script.echo "QEMU Validation"
    // }

    // def flashingM7(Map env, Map stageInput = [:]) {
    //     // add Flashing M7 Software Application stages here
    //     script.echo "Flashing M7 Software Application"
        
    // }

    // def ValidationHiL() {
    //     // add Validation of HiL stages here
    //     script.echo "Validation of HiL"
    // }

    // def Softwarepackage(Map env, Map stageInput = [:]) {
    //     // add S/W Package Artifacts stages here
    //     script.echo "S/W Package Artifacts"
    // }

    // def Vehiclepackage(Map env, Map stageInput = [:]) {
    //     // add S/W Package Artifacts stages here
    //     script.echo "Vehicle Package Artifacts"
    // }

    // def PANveriforVehicle(Map env, Map stageInput = [:]) {
    //     // add PANTARIS Integration and verification for Vehiclestages here
    //     script.echo "PANTARIS Integration and verification for Vehicle"
    // }

    // def PANverififorDevice(Map env, Map stageInput = [:]) {
    //     // add PANTARIS Integration and verification for Device here
    //     script.echo "PANTARIS Integration and verification for Device"

    // } 
    
    // def PANverififorDesiredStates(Map env, Map stageInput = [:]) {
    //     // add PANTARIS Integration and verification for Desired States here
    //     script.echo "PANTARIS Integration and verification for Device"

    // }


    

}


