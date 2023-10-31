package unitTests

import com.refSolution.pipelinerdepot.stages.CommonStages
import org.junit.Before

import org.junit.Test
import org.junit.Assert
import com.bosch.pipeliner.LoggerDynamic

class StagesTest {

    private ScriptEnvironment script
    private CommonStages commonStages 
    private Map<String, String> env
    private LoggerDynamic logger

    @Before
    void setUp() {
    env = [:]
    script = new ScriptEnvironment(env)
    Map stageInput = ["Build": "Number of 'Build'"]
    commonStages = new CommonStages(script, stageInput)
    logger = new LoggerDynamic(script)
    }


    @Test
    void teststageArchiveStages() {
    Map stageInput = ["Build": "Number of 'Build'"]
    commonStages.stageBuild(stageInput)

    assert script.filter_calls("testStage").size() == 0


    
    
    
  }   

}
  

