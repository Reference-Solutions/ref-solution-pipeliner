
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.refSolution.pipelinerdepot.utils.Dac

/**
* Contains stages that can be reused across pipelines
*/
class CommonDacStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private Dac dac

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    CommonDacStages(script, Map env) {
        this.script = script
        this.env = env
        this.dac = new Dac(script, env)
        this.logger = new LoggerDynamic(script)
    }

    def stageDacBuild(Map env, Map stageInput = [:]){
        script.stage("Dac Build") { 
            logger.info('Building document')
            String docsPath = stageInput.docsPath ? stageInput.docsPath : env.docsPath
            //String docsPath = env.docsPath
            String dac_build = stageInput.doc_build
            logger.info(dac_build)
            if (dac_build == "Sphinx"){
                dac.sphinxBuildDac(docsPath)
            } else if (dac_build == "doxygen"){
                logger.error("method not found")
            }
            
        }
    }
    def stageDacPublish(Map env, Map stageInput = [:]){
        script.stage("DAC publish to Github page")  {
            String docsPath = env.docsPath
            String docsurl = env.docsurl
            String ghFolder = env.docs_repo_name
            String html_source_path = env.html_source_path
            String html_destination_path = env.html_destination_path
            String dac_publish = stageInput.doc_publish
            logger.info("copyPath:" + docsPath)
            logger.info("ghfolder:" + ghFolder)
            logger.info("html_source_path:" + html_source_path)
            logger.info("html_destination_path:" + html_destination_path)
            logger.info(dac_publish)
            if (dac_publish == "github_page"){
                dac.ghPublishDac(docsurl, ghFolder, html_source_path ,html_destination_path)
            } else if (dac_publish == "Pantaris_cloud"){
                logger.error("method not found")
            }
            
        }
    
    }
}
