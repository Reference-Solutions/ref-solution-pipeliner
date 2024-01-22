package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.CommonStages
import com.refSolution.pipelinerdepot.stages.CommonGitStages
import com.refSolution.pipelinerdepot.stages.CommonSonarStages
import com.refSolution.pipelinerdepot.stages.CommonArchiveStages
import com.refSolution.pipelinerdepot.stages.CommonVersioningStages
import com.refSolution.pipelinerdepot.stages.CommonDacStages
import com.refSolution.pipelinerdepot.stages.CommonArtifactoryStages
import com.refSolution.pipelinerdepot.stages.ArcBswStages
import com.refSolution.pipelinerdepot.stages.QnxStages
import com.refSolution.pipelinerdepot.stages.VrteStages
import com.refSolution.pipelinerdepot.stages.FlashingStages
import com.refSolution.pipelinerdepot.stages.OtaNgStages

class SwFactoryPipeline extends BasePipeline {
    CommonGitStages commonGitStages
    CommonSonarStages commonSonarStages
    CommonArchiveStages commonArchiveStages
    CommonDacStages commonDacStages
    CommonVersioningStages commonVersioningStages
    CommonArtifactoryStages commonArtifactoryStages
    QnxStages qnxStages
    ArcBswStages arcBswStages
    VrteStages vrteStages
    FlashingStages flashingStages
    OtaNgStages otaNgStages

    Boolean skipPipeline = false

    SwFactoryPipeline(script, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: """
                checkout_scm_stage = true
                checkout_stage = true
                sonar_stage = true
                qnx_build_stage = true
                arc_build_stage = true
                vrte_pull_stage = true
                flashing_vip_stage = true
                versioning_stage = true
                archive_stage = true
                dac_stage = true
                artifactory_upload_stage = true
                sw_package_creation_stage = true
                desired_state_creation_stage = true
                device_creation_stage = true
                vehicle_creation_stage = true
                label = windows-lab-pc
                artifact_version
                archive_patterns
            """,
            // the keys exposed to the user for modification
            exposed: [
                'checkout_scm_stage',
                'checkout_stage',
                'sonar_stage',
                'qnx_build_stage',
                'arc_build_stage',
                'vrte_pull_stage',
                'flashing_vip_stage',
                'versioning_stage',
                'archive_stage',
                'artifactory_upload_stage',
                'sw_package_creation_stage',
                'desired_state_creation_stage',
                'device_creation_stage',
                'vehicle_creation_stage',
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
                'sonarPropertyFilePath',
                'artifact_version',
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
                'nexus_download_dir',
                'custom_scm_checkout_dir',
                'qnx_sdk_path',
              	'pfe_copy',
                'build_dir_path',
                'bsw_dir_path',
                'bsw_pre_build_file_name',
                'autosar_tool',
                'autosar_tool_version',
                'autosar_tool_env',
                'project_variant',
                'qnx_src_dir',
                'swc_dir_path',
                'gen_swp_dir_path',
                'app_dir_path',
                'app_name',
                'app_version',
                'action_type',
                'vrtefs_tool_path',
                'vpkg_dir_path'
            ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        commonGitStages = new CommonGitStages(script, env)
        commonSonarStages = new CommonSonarStages(script, env)
        commonArchiveStages = new CommonArchiveStages(script, env)
        commonVersioningStages = new CommonVersioningStages(script, env)
        commonDacStages = new CommonDacStages(script, env)
        commonArtifactoryStages = new CommonArtifactoryStages(script, env)
        qnxStages = new QnxStages(script, env)
        arcBswStages = new ArcBswStages(script, env)
        vrteStages = new VrteStages(script, env)
        flashingStages = new FlashingStages(script, env)
        otaNgStages = new OtaNgStages(script, env)
    }

    // /**
    // * Provides implementation for stages
    // *
    // * @param A Map with the inputs for stages
    // */
    @Override
    void stages(Map stageInput) {
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
        if (stageInput.qnx_build_stage == "true")
            qnxStages.stageBuild(env,stageInput)
        if (stageInput.arc_build_stage == "true")
            arcBswStages.stageBuild(env,stageInput)
        if (stageInput.vrte_pull_stage == "true")
            vrteStages.stagePullArtifact(env,stageInput)
        if (stageInput.flashing_vip_stage == "true"){
            flashingStages.stageVerifyT32(env, stageInput)
            flashingStages.stageFlashingVIP(env, stageInput)
        }
        if (stageInput.versioning_stage == "true")
            commonVersioningStages.stageVersioningArtifacts(env,stageInput)
        if (stageInput.archive_stage == "true")
            commonArchiveStages.stageArchive(stageInput)
        if (stageInput.sw_package_creation_stage == "true"){
            otaNgStages.stageSwPackgeCreation(env, stageInput)
            otaNgStages.stageVehicePackgeCreation(env, stageInput)
        }
        if (stageInput.desired_state_creation_stage == "true"){
            otaNgStages.stageDesiredStateCreation(env, stageInput)
        }
        if (stageInput.device_creation_stage == "true"){
            otaNgStages.stageDeviceCreation(env, stageInput)
        }
        if (stageInput.vehicle_creation_stage == "true"){
            otaNgStages.stageVehicleCreation(env, stageInput)
        }
    }
}