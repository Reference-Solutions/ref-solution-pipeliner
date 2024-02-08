
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.refSolution.pipelinerdepot.utils.Git

/**
* Contains stages that can be reused across pipelines
*/
class CommonGitStages {

    /**
     * for shallow clone using gitlab_branch_source plugin, the minimum depth
     * has to be at least the number of commits in the MR + 1, so the latest
     * commit from main branch is also fetched, and the MR can be merged into it.
     * in case the value is not sets, perform a full clone
     */
    final int defaultShallowCloneDepth = -1

    private def script
    private Map env
    private LoggerDynamic logger
    private Git git

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    CommonGitStages(script, Map env) {
        this.script = script
        this.env = env
        this.git = new Git(script, env)
        this.logger = new LoggerDynamic(script)
    }

    Boolean isAllSet(List<String> variables) {
        for (variable in variables) {
            if (!isSet(variable)) {
                return false
            }
        }
        return true
    }

    Boolean isAnySet(List<String> variables) {
        for (variable in variables) {
            if (isSet(variable)) {
                return true
            }
        }
        return false
    }

    Boolean isSet(String variable) {
        return variable?.trim()
    }

    Boolean isSet(List variable) {
        return variable != null && variable.size() > 0
    }

    Boolean isTrue(String variable) {
        return isSet(variable) && variable.equalsIgnoreCase('true')
    }

    /**
     * Checks out the associated git repository and initializes and
     * updates any submodules contained in the repo.
     *
     * @param env Map of Jenkins environment files
     * @param stageInput Map of input parameters
     */
    def stageCheckout(Map env, Map stageInput = [:]) {
        script.stage("Checkout") {
            processInputsAndExecuteGit(env, stageInput, "checkout")
        }
    }

    def stageTag(Map env, Map stageInput = [:]) {
        script.stage("Git Tag") {
            processInputsAndExecuteGit(env, stageInput, "tag")
        }
    }

    /**
     * Creates the GitSCM extensions object based on defaults and stageInput
     *
     * @param stageInput Map of stage input parameters
     */
    private ArrayList getSCMExtensions(Map stageInput, String checkout_dir = '' ) {
        // if 'submodules_shallow' is enabled and 'submodules_depth' is not specified, defaults to 1
        // this will be ignored if 'submodules_shallow' is set to 'false'
        int submodules_depth = isSet(stageInput.submodules_depth) ? stageInput.submodules_depth as int : 1
        boolean submodules_shallow = isTrue(stageInput.submodules_shallow)
        boolean submodules_disable = isTrue(stageInput.submodules_disable)
        boolean submodules_recursive = isTrue(stageInput.submodules_recursive)
        boolean submodules_clone = isTrue(stageInput.submodules_clone)

        String scm_checkout_dir = stageInput.custom_scm_checkout_dir?.trim() ?: checkout_dir

        /**
         * if 'clone_shallow' is enabled and 'clone_depth' is not specified,
         * defaults to the value of 'defaultShallowCloneDepth'.
         * This will configuration will be ignored if 'clone_shallow' is set to 'false'
         */
        int clone_depth = isSet(stageInput.clone_depth) ? stageInput.clone_depth as int : defaultShallowCloneDepth
        boolean clone_shallow = isTrue(stageInput.clone_shallow)
        boolean clone_no_tags = isTrue(stageInput.clone_no_tags)
        String clone_reference = stageInput.clone_reference?.trim() ?: null

        ArrayList extensions = []
        if (submodules_clone)
            extensions << [$class: 'SubmoduleOption',
                parentCredentials: true,
                disableSubmodules: false,
                recursiveSubmodules: true,
                shallow: submodules_shallow,
                depth: submodules_depth]

        extensions << [$class: 'CloneOption',
            shallow: clone_shallow,
            depth: clone_depth,
            noTags: clone_no_tags,
            reference: clone_reference]

        extensions << [$class: 'RelativeTargetDirectory',
            relativeTargetDir: scm_checkout_dir]

        return extensions
    }

    /**
     * Checkout without a stage
     *
     *
     * @param env Map of Jenkins environment files
     * @param stageInput Map of input parameters
     */


    def stageCheckoutSCM(Map env, Map stageInput = [:]){
        script.stage("checkout SCM") {
            git.checkout()
        }
    }

    def  processInputsAndExecuteGit(Map env, Map stageInput = [:], def gitOption = "checkout"){
        def checkoutCount = env.CHECKOUT_COUNT ?env.CHECKOUT_COUNT.toInteger(): 1

        logger.info("env : " + env)
        String url
        String branch
        String checkoutCredentialsId
        String repoName
        ArrayList extensions

        for(int i = 1; i <= checkoutCount; i++){

            url = env["CHECKOUT_URL_${i}"] ?: null

            
            if(url != null){
                repoName = url.replaceAll(".*/|.git","")
                if (gitOption == "checkout"){
                    branch = env["CHECKOUT_BRANCH_${i}"] ?: "master"
                    checkoutCredentialsId = env["CHECKOUT_CREDENTIALS_ID_${i}"] ?: ""
                    extensions = getSCMExtensions(stageInput, repoName)

                    git.checkout(url , branch, checkoutCredentialsId, extensions)
                }
                else if (gitOption == "tag"){
                    String tagVersion = stageInput.build_version
                    String buildEnvironment = stageInput.build_env?.trim() ?: "Nightly"
                    if (buildEnvironment == "Nightly"){
                        tagVersion = "tag_" + tagVersion + "_" + buildDate() + "_N"
                    }
                    else if (buildEnvironment == "Dev"){
                        tagVersion = "tag_" + tagVersion + "_" + buildDate() + "_Dev"
                    }
                    else if (buildEnvironment == "Release"){
                        tagVersion = "tag_" + tagVersion
                    }
                    git.tag(repoName, tagVersion)
                }
            }
            else {
                logger.warn("### Git url not provided, So Git Stage is skipped ###")
            }
        }
    }
    String buildDate(String formatString = "yyyyMMddHHmmss") {
        return new Date(script.currentBuild.getStartTimeInMillis()).format(formatString).toString().trim()
    }
}
