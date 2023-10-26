package unitTests

import com.refSolution.pipelinerdepot.stages.CommonGitStages
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.Assert



class CommonGitStagesTest {

    private ScriptEnvironment script
    private CommonGitStages pip
    private Map<String, String> env

    @Before
    void setUp() {
        
        script = new ScriptEnvironment(env)
        pip = new CommonGitStages(script, env)
    }

    @Test
    void testStageCheckoutCountOne() {
        env = [
            "CHECKOUT_URL_1" : "https://domain/gitrepo.git",
            "CHECKOUT_BRANCH_1" : "master",
            "CHECKOUT_CREDENTIALS_ID_1" : 'credential',
            "CHECKOUT_COUNT" : 1
        ]
        Map stageInput = [:]
        this.pip.stageCheckout(env, stageInput)
        assert script.filter_calls('checkout').size() == 1
    }

    @Test
    void testStageCheckoutCountTwo() {
        env = [
            "CHECKOUT_URL_1" : "https://domain/gitrepo.git",
            "CHECKOUT_BRANCH_1" : "master",
            "CHECKOUT_CREDENTIALS_ID_1" : 'credential',
            "CHECKOUT_COUNT" : 2
        ]
        Map stageInput = [:]
        this.pip.stageCheckout(env, stageInput)
        assert script.filter_calls('checkout').size() == 2
    }

    @Test
    void testStageCheckoutUrl() {
        env = [
            "CHECKOUT_URL_1" : "https://domain/gitrepo_1.git",
            "CHECKOUT_BRANCH_1" : "master",
            "CHECKOUT_CREDENTIALS_ID_1" : 'credential',
            "CHECKOUT_URL_2" : "https://domain/gitrepo_2.git",
            "CHECKOUT_BRANCH_2" : "master",
            "CHECKOUT_CREDENTIALS_ID_2" : 'credential',
            "CHECKOUT_COUNT" : 2
        ]
        Map stageInput = [:]
        this.pip.stageCheckout(env, stageInput)
        assert script.filter_calls('checkout')[0][1].contains("'url':'https://domain/gitrepo_1.git'") == true
        assert script.filter_calls('checkout')[1][1].contains("'url':'https://domain/gitrepo_2.git'") == true
    }

    @Test
    void testStageCheckoutBranch() {
        env = [
            "CHECKOUT_URL_1" : "https://domain/gitrepo_1.git",
            "CHECKOUT_BRANCH_1" : "branch_1",
            "CHECKOUT_CREDENTIALS_ID_1" : 'credential_1',
            "CHECKOUT_URL_2" : "https://domain/gitrepo_2.git",
            "CHECKOUT_BRANCH_2" : null,
            "CHECKOUT_CREDENTIALS_ID_2" : 'credential_2',
            "CHECKOUT_COUNT" : 2
        ]
        Map stageInput = [:]
        this.pip.stageCheckout(env, stageInput)
        assert script.filter_calls('checkout')[0][1].contains("'branches':[['name':'*/branch_1']]") == true
        assert script.filter_calls('checkout')[1][1].contains("'branches':[['name':'*/master']]") == true
    }

    @Test
    void testStageCheckoutCredentialID() {
        env = [
            "CHECKOUT_URL_1" : "https://domain/gitrepo_1.git",
            "CHECKOUT_BRANCH_1" : "branch_1",
            "CHECKOUT_CREDENTIALS_ID_1" : 'credential_1',
            "CHECKOUT_URL_2" : "https://domain/gitrepo_2.git",
            "CHECKOUT_BRANCH_2" : null,
            "CHECKOUT_CREDENTIALS_ID_2" : 'credential_2',
            "CHECKOUT_COUNT" : 2
        ]
        Map stageInput = [:]
        this.pip.stageCheckout(env, stageInput)
        assert script.filter_calls('checkout')[0][1].contains("'credentialsId':'credential_1'") == true
        assert script.filter_calls('checkout')[1][1].contains("'credentialsId':'credential_2'") == true
    }
}
