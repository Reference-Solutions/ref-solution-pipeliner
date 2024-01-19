package com.refSolution.pipelinerdepot.pipelines

import com.bosch.pipeliner.BasePipeline
import com.refSolution.pipelinerdepot.stages.CommonArtifactoryStages



class CommonArtifactory extends BasePipeline {
    CommonArtifactoryStages commonArtifactoryStages
    
    Boolean skipPipeline = false

    CommonArtifactory(script, Map env, Map ioMap) {
        super(script, [
            // the input keys and their default values for the pipeline, can be
            // overridden by user inputs from either MR message or Jenkins env
            defaultInputs: '''
                artifactory_target = target
            ''',
            // the keys exposed to the user for modification
            exposed: [
                'artifactory_target',
                'artifactory_pattern',
                'artifactory_upload',
                'artifactory_upload_type',
                'artifactory_download',
                'artifactory_download_type',
                'conan_remote_name_to_upload',
                'conan_package_ref_to_upload',
                'nexus_tool_version',
                'nexus_protocol',
                'nexus_url',
                'nexus_group_Id',
                'build_version',
                'nexus_repository',
                'nexus_credentials_id',
                'nexus_project_name',
                'nexus_classifier',
                'nexus_file_pattern',
                'nexus_packaging',
                'nexus_download_dir',
                'github_repo',
                'github_owner',
                'github_tag',
                'github_releasename',
                'github_avh_repo',
                'github_avh_owner',
                'github_avh_tag',
                'github_avh_releasename'
                 
            ],
            // the keys for which pipeline should be parallelized
            parallel: []
        ] as Map, env, ioMap)

        // Specify the node label expression
        // Looks like we can't use && syntax due to input parser
        nodeLabelExpr = "windows-lab-pc"

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
        // Skip the entire pipeline if we promote and there are no changes
        if (skipPipeline) {
            return
        }
        logger.info("stageInput")
        logger.info(stageInput.inspect())

        String artifactoryUpload = stageInput.artifactory_upload?.trim() ?: "false"
        String artifactoryDownload = stageInput.artifactory_download?.trim() ?: "false"

        if (artifactoryUpload == "true")
            commonArtifactoryStages.stageArtifactoryUpload(env, stageInput)
        if (artifactoryDownload == "true")
            commonArtifactoryStages.stageArtifactoryDownload(env, stageInput)
    }
}