// CommonMplPipeline.groovy

import com.refSolution.pipelinerdepot.pipelines.QnxPipeline

// Create an instance of QnxPipeline
def qnxPipeline = new QnxPipeline(this, env, ioMap)

// Access or modify pipeline properties if needed
qnxPipeline.archive_patterns = 'new-patterns/*.ui'

// Run common stages
qnxPipeline.commonStages.runCommonStages()

// Run custom stages for QNX pipeline
qnxPipeline.getCustomStages()