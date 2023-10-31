package unitTests

import com.refSolution.pipelinerdepot.stages.QnxStages
import org.junit.Before
import org.junit.Test
import org.junit.Assert

class QnxStagesTest {

    private ScriptEnvironment script
    private QnxStages qnxStages
    private Map<String, String> env

    @Before
    void setUp() {
        script = new ScriptEnvironment(env)
        qnxStages = new QnxStages(script, env)
    }

    @Test
    void testStageBuild() {
        Map stageInput = [:]
        qnxStages.stageBuild(env, stageInput)

        assert script.filter_calls_with_arguments('stage', 'Copy PFE').size() == 1
        assert script.filter_calls_with_arguments('stage', 'Build').size() == 1

    }

    @Test
    void testMakeBuild() {
        env = [
            qnx_sdk_path: 'C:/Users/Public/qnx_7.0.0',
            custom_scm_checkout_dir: ''
        ]
        Map stageInput = [:]
        qnxStages.makeBuild(env, stageInput)

        assert script.filter_calls_with_arguments('bat', "echo 'Set QNX env variable'").size() == 1
        assert script.filter_calls_with_arguments('bat', "call ${env.qnx_sdk_path}/qnxsdp-env.bat").size() == 1
        assert script.filter_calls_with_arguments('bat', "echo 'starting building'").size() == 1
        assert script.filter_calls_with_arguments('bat', "cd ${env.custom_scm_checkout_dir}").size() == 1
        assert script.filter_calls_with_arguments('bat', "make all").size() == 1

    }

    @Test
    void testCopyPFE() {
        env = [
            qnx_sdk_path: 'C:/Users/Public/qnx_7.0.0',
            custom_scm_checkout_dir: ''
        ]
        Map stageInput = [:]
        qnxStages.copyPFE(env, stageInput)

        assert script.filter_calls_with_arguments('powershell', "cd ${env.custom_scm_checkout_dir}").size() == 1
        assert script.filter_calls_with_arguments('powershell', "Copy-Item -Path 'pfe_1_1_0/*' -Destination 'pfe/' -Recurse -force").size() == 1

    }
}