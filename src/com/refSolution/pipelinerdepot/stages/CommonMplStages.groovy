package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.bosch.pipeliner.ScriptUtils
import com.refSolution.pipelinerdepot.stages.CommonStages


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

    def vrtePull(Map env, Map stageInput = [:]) {
        // add vrte pull stages here
        script.echo "Vrte pull"
    }
    
    def stageBuild(Map env, Map stageInput = [:]){
      	
      	String pfeCopy = stageInput.pfe_copy?.trim() ?: 'false'
        if(pfeCopy == "true"){
            script.stage("Copy PFE") {  
              copyPFE(env, stageInput)
            }
        }
        script.stage("Build") {
            makeBuild(env, stageInput)
        }
    }

    def makeBuild(Map env, Map stageInput = [:]){
        String qnxSdkPath = stageInput.qnx_sdk_path?.trim() ?: 'C:/Users/zrd2kor/qnx710'
        String scm_checkout_dir = stageInput.custom_scm_checkout_dir?.trim() ?: ''
        script.bat """
            echo 'Set QNX env variable'
            call ${qnxSdkPath}/qnxsdp-env.bat
            echo 'starting building'
            cd ${scm_checkout_dir}
            make all
        """
    }

    def copyPFE(Map env, Map stageInput = [:]){
        String scm_checkout_dir = stageInput.custom_scm_checkout_dir?.trim() ?: 'C:/Users/zrd2kor/qnx710'
        script.powershell """
            cd ${scm_checkout_dir}
            Copy-Item -Path 'pfe_1_1_0/*' -Destination 'pfe/' -Recurse -force
        """
    }
    
    def OPDAVHApplications(Map env, Map stageInput = [:]) {
        // add OPD/AVH Applications stages here
        script.echo "OPD/AVH Applications"
    }


    def OQEMUValidation(Map env, Map stageInput = [:]) {
        // add QEMU Validation stages here
        script.echo "QEMU Validation"
    }

    def FlashingM7(Map env, Map stageInput = [:]) {
        // add Flashing M7 Software Application stages here
        script.echo "Flashing M7 Software Application"
        
    }

    def ValidationHiL() {
        // add Validation of HiL stages here
        script.echo "Validation of HiL"
    }

    def Softwarepackage(Map env, Map stageInput = [:]) {
        // add S/W Package Artifacts stages here
        script.echo "S/W Package Artifacts"
    }

    def Vehiclepackage(Map env, Map stageInput = [:]) {
        // add S/W Package Artifacts stages here
        script.echo "Vehicle Package Artifacts"
    }

    def PANveriforVehicle(Map env, Map stageInput = [:]) {
        // add PANTARIS Integration and verification for Vehiclestages here
        script.echo "PANTARIS Integration and verification for Vehicle"
    }

    def PANverififorDevice(Map env, Map stageInput = [:]) {
        // add PANTARIS Integration and verification for Device here
        script.echo "PANTARIS Integration and verification for Device"

    } 
    
    def PANverififorDesiredStates(Map env, Map stageInput = [:]) {
        // add PANTARIS Integration and verification for Desired States here
        script.echo "PANTARIS Integration and verification for Device"

    }


    

}


