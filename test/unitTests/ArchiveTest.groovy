package unitTests

import com.refSolution.pipelinerdepot.stages.CommonArchiveStages
import org.junit.Before

import org.junit.Test
import org.junit.Assert
import com.bosch.pipeliner.LoggerDynamic

class ArchiveStagesTest {

    private ScriptEnvironment script
    private CommonArchiveStages commonArchiveStages 
    private Map<String, String> env
    private LoggerDynamic logger

    @Before
    void setUp() {
    env = [:]
    script = new ScriptEnvironment(env)
    Map stageInput = ["archive_patterns": "patterns"]
    commonArchiveStages = new CommonArchiveStages(script, stageInput)
    logger = new LoggerDynamic(script)
    }


    @Test
    void teststageArchiveStages() {
    Map stageInput = ["archive_patterns": "patterns"]
    commonArchiveStages.stageArchive(stageInput)
    
    assert script.filter_calls("archiveArtifacts").size() == 1
    
    
    
  }   

}
  

