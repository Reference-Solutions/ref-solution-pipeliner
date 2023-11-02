package com.refSolution.pipelinerdepot.utils

import com.bosch.pipeliner.LoggerDynamic
import com.cloudbees.groovy.cps.NonCPS

/**
 * This class provides helper functions to improve Jenkins scripting
 *

 */

public class Qemu {
    /**
     * The script object instance from Jenkins
     */
    private def script
    /**
     * Logger object. Needs to be dynamic to display messages after the Jenkins master restart.
     */
    private LoggerDynamic logger

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Reference to the Jenkins environment
     */
    public Qemu(def script, def env) {
        this.script = script
        this.logger = new LoggerDynamic(script)
    }

    def loadVrteOnQemu (String vrte_qemu_dir,String vrte_arch_type,String vrte_script_name){
            script.bat """
                cd ${vrte_qemu_dir}
                start ${vrte_script_name} ${vrte_arch_type}
                sleep 60
            """
    }

    def roboTest (String robot_options,String robot_test_dir  ) {
            script.bat """
            robot --outputdir reports ${robot_options} ${robot_test_dir}
            """  
    }

}