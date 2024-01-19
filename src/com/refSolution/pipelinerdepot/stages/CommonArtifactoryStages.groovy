
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.refSolution.pipelinerdepot.utils.Jfrog
import com.refSolution.pipelinerdepot.utils.Conan
import com.refSolution.pipelinerdepot.utils.Nexus
import com.refSolution.pipelinerdepot.utils.Github

/**
* Contains stages that can be reused across pipelines
*/
class CommonArtifactoryStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private Conan conan
    private Nexus nexus
    private Jfrog jfrog
    private Github github

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    CommonArtifactoryStages(script, Map env) {
        this.script = script
        this.env = env
        this.jfrog = new Jfrog(script, env)
        this.conan = new Conan(script, env)
        this.nexus = new Nexus(script, env)
        this.github = new Github(script, env)
        this.logger = new LoggerDynamic(script)
    }

    def stageArtifactoryDownload(Map env, Map stageInput = [:]){
        script.stage("Artifactory Downlaod") { 
            String downloadType = stageInput.artifactory_download_type?.trim()
            if (downloadType == "jfrog"){
                logger.info('DOWNLOAD FROM ARTIFACTORY STAGE')
                String pattern = stageInput.artifactory_pattern.trim()
                String target = stageInput.artifactory_target.trim()
                jfrog.downloadFromArtifactory(pattern, target)
            }
            else if (downloadType == "nexus"){
                logger.info('DOWNLOAD FROM NEXUS ARTIFACTORY STAGE')
                
                String nexusUrl = stageInput.nexus_url?.trim() ?: "http://10.165.38.203:8081"
                String groupId = stageInput.nexus_group_Id.trim()
                String version = stageInput.build_version.trim()
                String repository = stageInput.nexus_repository.trim()
                String credentialsId = stageInput.nexus_credentials_id.trim()
                String projectName = stageInput.nexus_project_name.trim()
                String targetDirectory = stageInput.nexus_download_dir?.trim() ?: "target"
                String fileNameRegex = stageInput.nexus_file_pattern.trim()
                String packaging = stageInput.nexus_packaging?.trim() ?: "zip"

                nexus.download(nexusUrl, repository, groupId, projectName, version, credentialsId, packaging, targetDirectory, fileNameRegex)
            }
// ***********
            else if (downloadType == "github"){
                logger.info('DOWNLOAD FROM  GITHUB RELEASE')
                String tag = stageInput.github_tag.trim()
                String owner = stageInput.github_owner.trim()
                String repo = stageInput.github_repo.trim()
                String releaseName = stageInput.github_OPD_release_name.trim()

                github.deleteFolderIfExists(releaseName)
                github.downloadLatestRelease(tag,owner,repo,releaseName)
                
    
            }

            else{
                logger.warn('Artifactory Download is Skipped since Download Type is not proper. It sould be jfrog/nexus')
            }

        }
    }

    def stageArtifactoryUpload(Map env, Map stageInput = [:]){
        script.stage("Artifactory Upload") { 
            String uploadType = stageInput.artifactory_upload_type?.trim()
            if (uploadType == "jfrog"){
                logger.info('Upload To Jfrog Artifactpty')
                String pattern = stageInput.artifactory_upload_pattern.trim()
                String target = stageInput.artifactory_upload_target.trim()
                jfrog.uploadToArtifactory(pattern, target)
            }
            else if (uploadType == "nexus"){
                logger.info('Upload to Nexus Artifactory')
                String nexusVersion = stageInput.nexus_tool_version?.trim() ?: "nexus3"
                String protocol = stageInput.nexus_protocol?.trim() ?: "http"
                String nexusUrl = stageInput.nexus_url?.trim() ?: "http://10.165.38.203:8081"
                
                String groupId = stageInput.nexus_group_id.trim()
                String version = stageInput.build_version.trim()
                String repository = stageInput.nexus_repository.trim()
                String credentialsId = stageInput.nexus_credentials_id.trim()
                String projectName = stageInput.nexus_project_name.trim()
                String classifier = stageInput.nexus_classifier?.trim() ?: ""
                String filePattern = stageInput.nexus_file_pattern.trim()
                String packaging = stageInput.nexus_packaging?.trim() ?: "zip"
                
                nexus.upload(nexusVersion, protocol, nexusUrl, groupId, version, repository, credentialsId, projectName, classifier, filePattern, packaging)
            }
            else if (uploadType == "conan"){
                logger.info('Artifactory upload using conan')
                String remoteName = stageInput.conan_remote_name_to_upload.trim()
                String packageRef = stageInput.conan_package_ref_to_upload.trim()
                conan.upload(remoteName, packageRef)
            }
            else{
                logger.warn('Artifactory upload is Skipped since Upload Type is not proper. It sould be jfrog/nexus/conan')
            }
        }
    }
}
