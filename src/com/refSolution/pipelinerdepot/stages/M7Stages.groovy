package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.bosch.pipeliner.ScriptUtils
import com.refSolution.pipelinerdepot.stages.CommonStages


/**
* Contains stages that can be reused across pipelines
*/
class M7Stages {

    private def script
    private Map env
    private LoggerDynamic logger
    private ScriptUtils utils

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    M7Stages(script, Map env) {
        this.script = script
        this.env = env
        this.logger = new LoggerDynamic(script)
        this.utils = new ScriptUtils(script, env)
    }
    
    def stageBuild(Map env, Map stageInput = [:]){
        script.stage("BSW Apply Patches") {
            bswPatches(env, stageInput)
        }
        script.stage("BSW Generation") {
            bswGenerationAeeePro(env, stageInput)
        }
        script.stage("Generate Libraries") {
            generateLibraries(env, stageInput)
        }
        script.stage("Build") {
            build(env, stageInput)
        }
    }

    def build(Map env, Map stageInput = [:]){
        String buildRepo = stageInput.build_repo.trim()
        script.bat """
            cd ${buildRepo}
            python build.py --variant vip-mainboard --subsystem s32g-uC --os arc --verbose
        """
    }

    def bswPatches(Map env, Map stageInput = [:]){
        //logger.info("bswRepo : ${stageInput.inspect()}")
        String bswRepo = stageInput.bsw_repo.trim()
        script.bat """
            cd ${bswRepo}
            call Pre_Build.bat
        """
    }

    def bswGenerationAeeePro(Map env, Map stageInput = [:]){
        String autosarProject = stageInput.bsw_repo.trim()
        String autosarTool = stageInput.autosar_tool.trim()
        String autosarToolVersion = stageInput.autosar_tool_version.trim()
        String autosarToolEnv = stageInput.autosar_tool_env.trim()
        String projectVariant = stageInput.project_variant.trim()
        script.bat """
            call tini -useEnv:cdg.de ${autosarTool} ${autosarToolVersion}
            call ${autosarTool} -convertbcttoabacus -p ${autosarProject} -m ${projectVariant}
            call ${autosarTool} -cdgb rebuild -p ${autosarProject} -m ${projectVariant}
        """
    }

    def generateLibraries(Map env, Map stageInput = [:]){
        String bswRepo = stageInput.bsw_repo?.trim()
        script.bat """
            cd ${bswRepo}
            call Build.bat
        """
    }
}
