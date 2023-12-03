
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.bosch.pipeliner.ScriptUtils

/**
* Contains stages that can be reused across pipelines
*/
class CommonVehicleStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private ScriptUtils utils
   

    /**
     * Constructor
     ******
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    CommonVehicleStages(script, Map env) {
        this.script = script
        this.env = env
        this.utils = new ScriptUtils(script, env)
        this.logger = new LoggerDynamic(script)
    }



    def stageVehiclepackage(Map env, Map stageInput = [:]) {
        script.stage("create a vehcile package") {
            String vec_package_path = stageInput.vec_package_dir?.trim() ?: ''
            script.bat """
            echo "${vec_package_path}"
            tar -cvf signedcontainer.tar "${vec_package_path}"
            echo > signature.crt
            tar -cvf vehiclepkg.tar signedcontainer.tar signature.crt
            """

        }

    }  

}