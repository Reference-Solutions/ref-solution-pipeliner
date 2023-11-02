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
            if (vrte_arch_type == "qemu-system-x86_64"){
              String vrte_script_name = "qemu-x86_64.bat"  
              qemu.loadVrteOnQemu(vrte_qemu_dir,vrte_arch_type,vrte_script_name)
            } else if (vrte_arch_type == "arm"){
                logger.error("method not found")
            }
            
            
        }    
    }

    def StageTest(Map env, Map stageInput = [:]) {
        script.stage("Stage Test"){
            String robot_options = stageInput.robot_options
            String robot_test_dir = stageInput.robot_test_dir
            qemu.roboTest(robot_options, robot_test_dir)
        }    
            
    }

    def stagePublishReport(Map env, Map stageInput = [:]){
        script.stage("Publish HTML Report") { 
            logger.info("Post stage Test")
            script.publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'reports/', reportFiles: 'report.xml', reportName: 'HTML Report', reportTitles: '', useWrapperFileDirectly: true])
        }
    }
    
    
}
