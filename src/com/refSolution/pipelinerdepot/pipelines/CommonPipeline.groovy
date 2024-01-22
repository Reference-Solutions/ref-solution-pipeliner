package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.CommonStages
import com.refSolution.pipelinerdepot.stages.CommonGitStages
import com.refSolution.pipelinerdepot.stages.CommonSonarStages
import com.refSolution.pipelinerdepot.stages.CommonArchiveStages
import com.refSolution.pipelinerdepot.stages.CommonVersioningStages
import com.refSolution.pipelinerdepot.stages.CommonDacStages
import com.refSolution.pipelinerdepot.stages.CommonArtifactoryStages


class CommonPipeline extends BasePipeline {
    CommonGitStages commonGitStages
    CommonSonarStages commonSonarStages
    CommonArchiveStages commonArchiveStages
    CommonDacStages commonDacStages
    CommonVersioningStages commonVersioningStages

    
    Boolean skipPipeline = false

    CommonPipeline(script, Map defaults, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: """
                checkout_scm_stage = true
                checkout_stage = true
                git_tag_stage = false
                sonar_stage = true
                build_stage = true
                versioning_stage = true
                archive_stage = true
                dac_stage = true
                artifactory_upload_stage = true
                label = windows-lab-pc
            """ + defaults.defaultInputs,
            // the keys exposed to the user for modification
            exposed: [
                'checkout_scm_stage',
                'checkout_stage',
                'git_tag_stage',
                'sonar_stage',
                'build_stage',
                'versioning_stage',
                'archive_stage',
                'artifactory_upload_stage',
                'submodules_depth',
                'submodules_shallow',
                'submodules_disable',
                'submodules_recursive',
                'submodules_clone',
                'custom_scm_checkout_dir',
                'clone_depth',
                'clone_shallow',
                'clone_no_tags',
                'clone_reference',
                'sonar_property_file_path',
                'build_version',
                'build_env',
                'archive_patterns',
                'dac_stage',
                'doc_build', 
                'doc_publish',
                'docsurl',
                'artifactory_upload_type',
                'conan_remote_name_to_upload',
                'conan_package_ref_to_upload',
                'nexus_tool_version',
                'nexus_protocol',
                'nexus_url',
                'nexus_group_id',
                'build_version',
                'nexus_repository',
                'nexus_credentials_id',
                'nexus_project_name',
                'nexus_classifier',
                'nexus_file_pattern',
                'nexus_packaging',
                'nexus_download_dir'
            ] + defaults.exposed,
            // the keys for which pipeline should be parallelized
            parallel: [] + defaults.parallel
        ] as Map, env, ioMap)

        commonGitStages = new CommonGitStages(script, env)
        commonSonarStages = new CommonSonarStages(script, env)
        commonArchiveStages = new CommonArchiveStages(script, env)
        commonVersioningStages = new CommonVersioningStages(script, env)
        commonDacStages = new CommonDacStages(script, env)
        commonArtifactoryStages = new CommonArtifactoryStages(script, env)

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
        if (stageInput.checkout_scm_stage == "true")
            commonGitStages.stageCheckoutSCM(env, stageInput)
        if (stageInput.checkout_stage == "true")
            commonGitStages.stageCheckout(env, stageInput)
        if (stageInput.sonar_stage == "true")
            commonSonarStages.stageSonarAnalysis(env,stageInput)
        if (stageInput.build_stage == "true")
            customStages.stageBuild(env,stageInput)
        if (stageInput.versioning_stage == "true")
            commonVersioningStages.stageVersioningArtifacts(env,stageInput)
        if (stageInput.archive_stage == "true")
            commonArchiveStages.stageArchive(stageInput)
        if (stageInput.dac_stage == "true"){
            commonDacStages.stageDacBuild(env, stageInput)
            commonDacStages.stageDacPublish(env, stageInput)
        }
        if (stageInput.artifactory_upload_stage == "true")  
            commonArtifactoryStages.stageArtifactoryUpload(env, stageInput)
        if (stageInput.git_tag_stage == "true")  
            commonGitStages.stageTag(env, stageInput)
    }

    void getCustomStages(){
        CommonStages customStages = new CommonStages(script, env)
    }
}