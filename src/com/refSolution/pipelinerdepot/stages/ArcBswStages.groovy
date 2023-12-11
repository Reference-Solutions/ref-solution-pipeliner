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

        String bswBuild = stageInput.bsw_build?.trim() ?: "false"
        String aswBuild = stageInput.asw_build?.trim() ?: "false"
        
        script.stage("BSW Apply Patches") {
            String bswDirPath = stageInput.bsw_build_dir_path.trim()
            String bswPreBuildFileName = stageInput.bsw_pre_build_file_name?.trim() ?: "PreBuild.bat"
            callBatFile(bswDirPath, bswPreBuildFileName)
        }
        script.stage("BSW Generation") {
            String autosarTool = stageInput.autosar_tool.trim()
            if (autosarTool == "aeee_pro"){
                bswGenerationAeeePro(env, stageInput)
            }
        }
        logger.info("env : " + env) 
        logger.info("Script env : " + script.env)
        logger.info("This env : " + this.env)
        if (bswBuild == "true" && aswBuild == "true"){
            script.stage("BSW and ASW build") {            
                String bswBuildDirPath = stageInput.bsw_build_dir_path.trim()
                String bswBuildFileName = stageInput.bsw_build_file_name?.trim() ?: "Build.bat"
                String aswBuildDirPath = stageInput.asw_build_dir_path.trim()
                String aswBuildFileName = stageInput.asw_build_file_name?.trim() ?: "Build.bat"
                  script.bat """
                    cd ${script.env.WORKSPACE}/${bswBuildDirPath}
                    call ${bswBuildFileName}
                    cd ${script.env.WORKSPACE}/${aswBuildDirPath}
                    call ${aswBuildFileName}
                """
            }
        }
        else{
            if (bswBuild == "true"){
                script.stage("BSW build") {            
                    String bswBuildDirPath = stageInput.bsw_build_dir_path.trim()
                    String bswBuildFileName = stageInput.bsw_build_file_name?.trim() ?: "Build.bat"
                    callBatFile(bswBuildDirPath, bswBuildFileName)
                }
            }
            
            if (aswBuild == "true"){
                script.stage("ASW build") {  
                    String aswBuildDirPath = stageInput.asw_build_dir_path.trim()
                    String aswBuildFileName = stageInput.asw_build_file_name?.trim() ?: "Build.bat"
                    callBatFile(aswBuildDirPath, aswBuildFileName)
                }
            }
            script.stage("Integeration") {
                String integerationDirPath = stageInput.integeration_dir_path.trim()
                String integerationBuildFileName = stageInput.integeration_file_name?.trim() ?: "Build.bat"
                callBatFile(integerationDirPath, integerationBuildFileName)
            }
        }
    }

    def bswGenerationAeeePro(Map env, Map stageInput = [:]){
        String autosarProject = stageInput.bsw_build_dir_path.trim()
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

    def callBatFile(def batDirPath, def batFile){
        script.bat """
            cd ${batDirPath}
            call ${batFile}
        """
    }
}
