package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.bosch.pipeliner.ScriptUtils
import com.refSolution.pipelinerdepot.stages.CommonStages
import com.refSolution.pipelinerdepot.utils.Qemu


/**
* Contains stages that can be reused across pipelines
*/
class QemuStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private ScriptUtils utils
    private CommonStages commonStages
    private Qemu qemu 

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    QemuStages(script, Map env) {
        this.script = script
        this.env = env
        this.logger = new LoggerDynamic(script)
        this.utils = new ScriptUtils(script, env)
        this.commonStages = new CommonStages(script, env)
        this.qemu = new Qemu(script, env)
    }

    def StageLoadVRTE(Map env, Map stageInput = [:]) {
        script.stage("Load VRTE to Qemu"){    
            String vrte_qemu_dir = stageInput.vrte_qemu_dir
            String vrte_arch_type = stageInput.vrte_arch_type
            String vrte_script_name = stageInput.vrte_script_name
            qemu.loadVrteOnQemu(vrte_qemu_dir,vrte_arch_type,vrte_script_name)
            // if (vrte_arch_type == "qemu-system-x86_64"){
            //   String vrte_script_name = "qemu-x86_64.sh"  
            //   qemu.loadVrteOnQemu(vrte_qemu_dir,vrte_arch_type,vrte_script_name)
            // } else if (vrte_arch_type == "arm"){
            //     logger.error("method not found")
            // }
            
            
        }    
    }

    def StageTest(Map env, Map stageInput = [:]) {
        script.stage("Robot Test"){
            String robot_options = stageInput.robot_options
            String robot_test_dir = stageInput.robot_test_dir
            qemu.roboTest(robot_options, robot_test_dir)
        }    
            
    }

    def stagePublishReport(Map env, Map stageInput = [:]){
        script.stage("Publish HTML Report") { 
            logger.info("Post stage Test")
            script.publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'reports/', reportFiles: 'report.xml', reportName: 'HTML Report', reportTitles: 'VRTE Report', useWrapperFileDirectly: true])
        }
    }
    
}
