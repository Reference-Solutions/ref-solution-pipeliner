// CommonMplStages.groovy

import com.refSolution.pipelinerdepot.stages.QnxStages

// Create an instance of QnxStages
def qnxStages = new QnxStages(this, env)

// Use QnxStages methods as needed
qnxStages.stageBuild(env, [pfe_copy: 'true'])
