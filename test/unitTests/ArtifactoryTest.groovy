package unitTests

import com.refSolution.pipelinerdepot.utils.Artifactory

import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.Assert


class ArtifactoryTest {
    private ScriptEnvironment script
    private Artifactory artifactory
    //private Artifactory amock
    private Map<String, String> env


    @Before
    void setup(){
        //env = [:]
        env = [
            "ARTIFACTORY_SERVER_ID" : "artifactory-boschdevcloud",
            "artifactory_target" : "target",
            "artifactory_pattern" : "lab000019-dasy2-generic-local/vip/eth-switch-nxp-sja1110/0.1/"
        ]
       
        script = new ScriptEnvironment(env)
        artifactory = new Artifactory(script,env)
      

    }

    @Test
    void testArtifactoryDownload() {
        env = [
            "ARTIFACTORY_SERVER_ID" : "artifactory-boschdevcloud",
            "artifactory_target" : "target",
            "artifactory_pattern" : "lab000019-dasy2-generic-local/vip/eth-switch-nxp-sja1110/0.1/"
        ]
        Map stageInput = [:]
        String pattern = env.artifactory_pattern.trim()
        String target = env.artifactory_target.trim()
        this.artifactory.downloadFromArtifactory(pattern, target)
        assert script.filter_calls('artifactoryDownload').size() == 1
    }

    @Test
    void testArtifactoryUpload() {
        env = [
            "ARTIFACTORY_SERVER_ID" : "artifactory-demodevcloud",
            "artifactory_target" : "target",
            "artifactory_pattern" : "C:local/vip/comp_name/0.1/"
        ]
        Map stageInput = [:]
        def pattern = env.artifactory_pattern
        def target = env.artifactory_target
        this.artifactory.uploadToArtifactory(pattern, target)
        assert script.filter_calls('artifactoryUpload').size() == 1
    }

}