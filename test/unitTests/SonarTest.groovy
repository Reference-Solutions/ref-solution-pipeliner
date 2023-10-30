package unitTests

import com.refSolution.pipelinerdepot.stages.CommonSonarStages
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.Assert

class SonarQubeTest {

    private ScriptEnvironment script
    private CommonSonarStages commonSonarStages
    private Map<String, String> env

    @Before
    void setUp() {
        // Set up the environment variables as needed for your SonarQube stage
        env = [
            // Define your environment variables here
            "SONAR_PROPERTY_FILE_PATH": "sonar.properties"
        ]
        script = new ScriptEnvironment(env)
        commonSonarStages = new CommonSonarStages(script, env)
    }

    @Test
    void teststageSonarAnalysisWithsh() {
        Map stageInput = [:]
        this.commonSonarStages.stageSonarAnalysis(stageInput)
        assert script.filter_calls_with_arguments('sh', "sonar-scanner -Dproject.settings=${env.SONAR_PROPERTY_FILE_PATH}").size() == 1
        }

    @Test
    void teststageSonarAnalysisWithBat() {
        Map stageInput = [:]
        this.commonSonarStages.stageSonarAnalysis(stageInput)
        assert script.filter_calls_with_arguments('bat', "sonar-scanner -Dproject.settings=${env.SONAR_PROPERTY_FILE_PATH}").size() == 0
    }
}