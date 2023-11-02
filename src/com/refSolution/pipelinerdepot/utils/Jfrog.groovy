package com.refSolution.pipelinerdepot.utils

import com.bosch.pipeliner.LoggerDynamic
import com.bosch.pipeliner.ScriptUtils
import com.cloudbees.groovy.cps.NonCPS
import org.jfrog.hudson.pipeline.common.types.ArtifactoryServer
import org.jfrog.hudson.pipeline.common.types.PromotionConfig
import groovy.json.JsonSlurperClassic

/**
 * This class provides helper functions to improve Jenkins scripting
 */

public class Jfrog {
    /**
     * The artifactory object instance from Jenkins
     */
    private ArtifactoryServer artifactory = null

    /**
     * The script object instance from Jenkins
     */
    private def script

    /**
     * The env object instance from Jenkins
     */
    private def env

    /**
     * Logger object. Needs to be dynamic to display messages after the Jenkins master restart.
     */
    private LoggerDynamic logger

    
    private ScriptUtils utils

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Reference to the Jenkins environment
     */
    public Jfrog(def script, def env) {
        this.script = script
        this.env = env
        this.logger = new LoggerDynamic(script)
        this.utils = new ScriptUtils(script,env)
    }
    
    /**
     * Initializes Artifactory server instance
     *
     * @return Artifactory server instance or null in case of failure
     */
    ArtifactoryServer initArtifactory() {
        String artifactoryUrl
        String artifactoryServerId

        if (this.artifactory == null) {
            artifactoryUrl = this.env.ARTIFACTORY_URL ? this.env.ARTIFACTORY_URL : ""
            artifactoryServerId = this.env.ARTIFACTORY_SERVER_ID ? this.env.ARTIFACTORY_SERVER_ID : ""
            ArtifactoryServer server = null
            if (artifactoryUrl != "") {
                logger.info("Artifactory: " + artifactoryUrl)
                server = this.script.newArtifactoryServer(url: artifactoryUrl)
                server.credentialsId = getArtifactoryCredentialsFromEnvironment()
                server.bypassProxy = true
            } else if (artifactoryServerId != "") {
                logger.info("Artifactory Server Id: " + artifactoryServerId)
                server = this.script.getArtifactoryServer(artifactoryServerId)
            } else {
                logger.info("Artifactory: default-artifactory-server-id")
                server = this.script.getArtifactoryServer("default-artifactory-server-id")
            }
            this.artifactory = server
        }
        return this.artifactory
    }

    /**
     * Publishes build info to artifactory for a given set of uploaded artifacts
     * See details:
     * https://javadoc.jenkins.io/plugin/artifactory/org/jfrog/hudson/pipeline/common/types/buildInfo/BuildInfo.html
     *
     * @param buildInfo Build info data
     */
    void publishBuildInfoToArtifactory(def buildInfo) {
        script.publishBuildInfo(buildInfo: buildInfo, server: initArtifactory())
    }

    /**
     * Enables interactive promotion feature for Jenkins Artifactory plugin.
     * Adds `Artifactory Release Promotion` button to Jenkins user interface for current build.
     */
    void enableInteractiveArtifactPromotion() {
        initArtifactory()
        PromotionConfig pConfig = new PromotionConfig()
        pConfig.buildName = this.env.JOB_NAME.replaceAll('/', ' :: ')
        pConfig.buildNumber = this.env.BUILD_NUMBER.toString()
        this.script.addInteractivePromotion(server: this.artifactory, promotionConfig: pConfig)
    }

    /**
     * Upload files to artifactory server
     * See details:
     * https://javadoc.jenkins.io/plugin/artifactory/org/jfrog/hudson/pipeline/common/types/buildInfo/BuildInfo.html
     *
     * @param target Upload location in Artifactory
     * @param pattern A pattern that defines the files to upload
     * @param flat Boolean to indicate if upload folders are ignored
     * @param failNoOp Boolean to indicate if the function should fail if nothing is uploaded
     * @return BuildInfo for artifactory
     */
    def uploadToArtifactory(String pattern, String target, boolean flat = false, boolean failNoOp = true) {
        boolean recursive = pattern.contains('*') || isDirectory(pattern)

        def uploadSpec = """{ "files": [ { "pattern": "${pattern}", "target": "${target}", "excludePatterns": ["*.sha1","*.md5","*.sha256","*.sha512"], "flat": "${flat}", "recursive": "${recursive}" } ] }"""

        try {
            return script.artifactoryUpload(spec: uploadSpec, server: initArtifactory(), failNoOp: failNoOp)
        } catch (Exception ex) {
            logger.error("uploadToArtifactory: pattern: ${pattern} target: ${target} flat: ${flat} failNoOp: ${failNoOp}")
            throw ex
        }
    }

    /**
     * Download files from artifactory server with defined downloadSpec
     * See details:
     * https://javadoc.jenkins.io/plugin/artifactory/org/jfrog/hudson/pipeline/common/types/buildInfo/BuildInfo.html
     *
     * @param target Download location in Artifactory
     * @param pattern A pattern that defines the files to download
     * @param flat Boolean to indicate if download folders are ignored
     * @param failNoOp Boolean to indicate if the function should fail if nothing is downloaded
     * @return BuildInfo for artifactory
     */
    def downloadFromArtifactory(String pattern, String target, boolean flat = false, boolean failNoOp = true) {
        def downloadSpec = """{
            "files":[
                        {
                            "pattern": \"${pattern}\",
                            "target": \"${target}\",
                            "flat": "${flat}"
                        }
                    ]
            }"""

        try {
            return script.artifactoryDownload(spec: downloadSpec, server: initArtifactory(), failNoOp: failNoOp)
        } catch (Exception ex) {
            logger.error("downloadFromArtifactory: pattern: ${pattern} target: ${target} flat: ${flat} failNoOp: ${failNoOp}")
            throw ex
        }
    }

    def uploadToArtifactoryRaw(String pattern, String target, boolean flat = false, boolean failNoOp = true) {
        initArtifactory()

        boolean recursive = pattern.contains('*') || isDirectory(pattern)

        List files = recursive ? script.findFiles(glob: pattern) : [new File(pattern)]

        if (files.size() == 0 || (files.size() == 1 && !isFile(files[0].toString()))) {
            if (failNoOp) {
                script.error("uploadToArtifactoryRaw: no files found with pattern: ${pattern}")
            } else {
                return script.newBuildInfo()
            }
        }

        script.withCredentials([script.usernameColonPassword(credentialsId: artifactory.credentialsId, variable: "ARTIFACTORY_USERPASS")]) {
            files.each { file ->
                String targetPath = target
                if (recursive && !flat) {
                    targetPath = "${target}${file}"
                }
                String urlCmd = "'${artifactory.url}/${targetPath}'"
                String curlCmd = "${curlBaseCmd} -u '${script.ARTIFACTORY_USERPASS}' -X PUT -T ${file} ${urlCmd}"
                runCurl("upload", curlCmd)
            }
        }

        return script.newBuildInfo()
    }

    def downloadFromArtifactoryRaw(String source, String target, boolean flat = false, String exclude = 'index.html*') {
        initArtifactory()

        if (source.contains('*')) {
            script.error("Download source incompatible with wget: ${source}, pattern matching not supported")
        }

        int cutDirs = flat ? source.count("/") : 1

        String wgetBaseCmd = "wget -nv -r --no-parent --no-host-directories -e robots=off --auth-no-challenge -P ${target} --cut-dirs ${cutDirs} -R ${exclude}"

        script.withCredentials([script.usernamePassword(credentialsId: artifactory.credentialsId,
            usernameVariable: 'ARTIFACTORY_USERNAME', passwordVariable: 'ARTIFACTORY_PASSWORD')]) {

            String urlCmd = "'${artifactory.url}/${source}'"
            String wgetCmd = "${wgetBaseCmd} --http-user=${script.ARTIFACTORY_USERNAME} --http-password=${script.ARTIFACTORY_PASSWORD} ${urlCmd}"
            script.sh(wgetCmd)
        }

        return script.newBuildInfo()
    }

    private void runCurl(String methodName, String curlCmd) {
        String stringResult = script.sh(script: curlCmd, returnStdout: true)
        Map jsonResult = new JsonSlurperClassic().parseText(stringResult)
        List jsonResultErrors = jsonResult.messages.findAll { Map message -> message.level == 'ERROR' }
        if (jsonResultErrors) {
            String errorMessage = "Failed to ${methodName} artifact, the result error messages:\n\n"
            errorMessage += jsonResultErrors.collect { Map message -> message.message }.join('\n\n')
            script.error(errorMessage)
        }
        if (jsonResult.errors) {
            script.error(stringResult)
        }
    }

    private void copyMoveRestAPIWrapper(String methodName, String from, String to) {
        initArtifactory()

        script.withCredentials([script.usernameColonPassword(credentialsId: this.artifactory.credentialsId, variable: "ARTIFACTORY_USERPASS")]) {
            String urlCmd = "'${this.artifactory.url}/api/${methodName}/${from}?to=${to}'"
            String curlCmd = "${curlBaseCmd} -u '${script.ARTIFACTORY_USERPASS}' -X POST ${urlCmd}"

            logger.info("${methodName.capitalize()} artifact with the curl cmd '${curlCmd}'")
            runCurl(methodName, curlCmd)
        }
    }

    /**
     * Copy files in artifactory to another location
     *
     * @param from From path/file
     * @param to Destination path/file
     */
    void copyInArtifactory(String from, String to) {
        copyMoveRestAPIWrapper('copy', from, to)
    }

    /**
     * Move files in artifactory to another location
     *
     * @param from From path/file
     * @param to Destination path/file
     */
    void moveInArtifactory(String from, String to) {
        copyMoveRestAPIWrapper('move', from, to)
    }

    /**
     * Delete files in artifactory
     *
     * @param path Path of files to delete
     */
    void deleteInArtifactory(String path) {
        initArtifactory()

        script.withCredentials([script.usernameColonPassword(credentialsId: this.artifactory.credentialsId, variable: "ARTIFACTORY_USERPASS")]) {
            String urlCmd = "'${this.artifactory.url}/${path}'"
            String curlCmd = "${curlBaseCmd} -u '${script.ARTIFACTORY_USERPASS}' -X DELETE ${urlCmd}"

            logger.info("Delete artifacts with the curl cmd '${curlCmd}'")
            utils.shWithStdout(curlCmd)
        }
    }

    /**
     * Check if location exists in artifactory
     *
     * @param url of location
     */
    Boolean checkInArtifactory(String url) {
        initArtifactory()

        Integer status = -1

        script.withCredentials([script.usernameColonPassword(credentialsId: this.artifactory.credentialsId, variable: "ARTIFACTORY_USERPASS")]) {
            String cmd =    "function check { " +
                            "if curl -u '${script.ARTIFACTORY_USERPASS}' --output /dev/null --silent --head --fail \$1; " +
                            "then return 0; " +
                            "else return -1; fi; }; " +
                            "check ${url}"
            status = utils.shWithStatus cmd
        }
        if (status == 0) {
            logger.info("URL  " + url + " is found")
            return true
        }
        return false
    }

    /**
     * Convert property map into an URL encoded string
     *
     * @param properties Map<String,String> of property key-value pairs
     * @return String value of encoded properties
     */
    private String propertyMapToString(Map<String,String> properties) {
        String concatenated = ""
        properties.each { key,value ->
            concatenated += "${key}=${value}\n"
        }
        return concatenated.trim().replaceAll("\n","%7C")
    }

    /**
     * Add properties to artifactory files
     *
     * @param path Path of files to add the properties to
     * @param properties Map<String,String> of property key-value pairs
     * @param recursive Boolean value indicating if properties should be applied recursively
     */
    void addPropertiesInArtifactory(String path, Map<String,String> properties, Boolean recursive = false) {
        initArtifactory()

        script.withCredentials([script.usernameColonPassword(credentialsId: this.artifactory.credentialsId, variable: "ARTIFACTORY_USERPASS")]) {
            String urlCmd = "'${this.artifactory.url}/api/storage/${path}?properties=${propertyMapToString(properties)}&recursive=${recursive ? 1 : 0}'"
            String curlCmd = "${curlBaseCmd} -u '${script.ARTIFACTORY_USERPASS}' -X PUT ${urlCmd}"

            logger.info("Add properties to artifacts with the curl cmd '${curlCmd}'")
            utils.shWithStdout(curlCmd)
        }
    }

    /**
     * Initialize internal Artifactory credential id from environment
     * @return artifactoryCredentialsId
     */
    def getArtifactoryCredentialsFromEnvironment() {
        String artifactoryCredentialsId = ""
        try {
            artifactoryCredentialsId = this.env.ARTIFACTORY_CREDENTIALS_ID ? this.env.ARTIFACTORY_CREDENTIALS_ID : ""
            if (artifactoryCredentialsId.isEmpty()) {
                def split = this.env.JOB_NAME.tokenize("./")
                artifactoryCredentialsId = split[0] + "-artifactory"
            }
        } catch (NullPointerException) {
            logger.warn("Artifactory credential reading failed")
        }
        logger.info("Artifactory credentials: " + artifactoryCredentialsId)
        return artifactoryCredentialsId
    }
}