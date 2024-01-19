package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.bosch.pipeliner.ScriptUtils
import com.refSolution.pipelinerdepot.stages.CommonStages


/**
* Contains stages that can be reused across pipelines
*/
class ArcBswStages {

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
    ArcBswStages(script, Map env) {
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
            String autosarTool = stageInput.autosar_tool.trim()
            if (autosarTool == "aeee_pro"){
                bswGenerationAeeePro(env, stageInput)
            }
        }
        script.stage("Generate Libraries") {
            generateLibraries(env, stageInput)
        }
        script.stage("Build") {
            build(env, stageInput)
        }
    }

    def build(Map env, Map stageInput = [:]){
        String buildDirPath = stageInput.build_dir_path.trim()
        script.bat """
            cd ${buildDirPath}
            call Build.bat
        """
    }

    def bswPatches(Map env, Map stageInput = [:]){
        String bswDirPath = stageInput.bsw_dir_path.trim()
        String bswPreBuildFileName = stageInput.bsw_pre_build_file_name?.trim() ?: "PreBuild.bat"
        script.bat """
            cd ${bswDirPath}
            call ${bswPreBuildFileName}
        """
    }

    def bswGenerationAeeePro(Map env, Map stageInput = [:]){
        String autosarProject = stageInput.bsw_dir_path.trim()
        String autosarTool = stageInput.autosar_tool.trim()
        String autosarToolVersion = stageInput.autosar_tool_version.trim()
        String autosarToolEnv = stageInput.autosar_tool_env.trim()
        String projectVariant = stageInput.project_variant.trim()
        script.bat """
            mkdir aeee_pro_workspace
            call tini -useEnv:cdg.de ${autosarTool} ${autosarToolVersion}
            call ${autosarTool} -convertbcttoabacus -p ${autosarProject} -m ${projectVariant} -w aeee_pro_workspace
            call ${autosarTool} -cdgb rebuild -p ${autosarProject} -m ${projectVariant} -w aeee_pro_workspace
        """
    }

    def generateLibraries(Map env, Map stageInput = [:]){
        String bswDirPath = stageInput.bsw_dir_path?.trim()
        script.bat """
            cd ${bswDirPath}
            call Build.bat
        """
    }
}
