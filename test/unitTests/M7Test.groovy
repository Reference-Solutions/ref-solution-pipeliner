package unitTests

import com.refSolution.pipelinerdepot.stages.M7Stages
import org.junit.Before
import org.junit.Test
import org.junit.Assert

class M7StagesTest {

    private ScriptEnvironment script
    private M7Stages m7Stages
    private Map<String, String> env

    @Before
    void setUp() {
        env = [
            build_repo: 'vip-project-uC',
            bsw_repo: 'cubas-nxp-s32g',
            autosar_tool: 'aeee_pro',
            autosar_tool_version: '2021.2.0.3',
            autosar_tool_env: 'cdg.de',
            project_variant: 'VIP_MAIN_BOARD'
        ]
        script = new ScriptEnvironment(env)
        m7Stages = new M7Stages(script, env)
    }

    @Test
    void testStageBuild() {
        Map stageInput = [build_repo: 'vip-project-uC',
            bsw_repo: 'cubas-nxp-s32g',
            autosar_tool: 'aeee_pro',
            autosar_tool_version: '2021.2.0.3',
            autosar_tool_env: 'cdg.de',
            project_variant: 'VIP_MAIN_BOARD']
        m7Stages.stageBuild(env, stageInput)

        assert script.filter_calls_with_arguments('stage', 'BSW Apply Patches').size() == 1
        assert script.filter_calls_with_arguments('stage', 'BSW Generation').size() == 1
        assert script.filter_calls_with_arguments('stage', 'Generate Libraries').size() == 1
        assert script.filter_calls_with_arguments('stage', 'Build').size() == 1

    }

    @Test
    void testBuild() {
        Map stageInput = [build_repo: "vip-project-uC"]
        m7Stages.build(env, stageInput)

        assert script.filter_calls_with_arguments('bat', "cd ${env.build_repo}").size() == 1
        assert script.filter_calls_with_arguments('bat', "python build.py --variant vip-mainboard --subsystem s32g-uC --os arc --verbose").size() == 1
        

    }

    @Test
    void testBSWApplyPatches() {
        Map stageInput = [bsw_repo: "cubas-nxp-s32g"]
        m7Stages.bswPatches(env, stageInput)

        assert script.filter_calls_with_arguments('bat', "cd ${env.bsw_repo}").size() == 1
        assert script.filter_calls_with_arguments('bat', "call Pre_Build.bat").size() == 1

    }
    @Test
    void testBSWGeneration() {
        Map stageInput = [autosar_tool: "aeee_pro" , autosar_tool_version: "2021.2.0.3" , bsw_repo: "cubas-nxp-s32g" ,  project_variant: "VIP_MAIN_BOARD" , autosar_tool_env: "cdg.de"]
        m7Stages.bswGenerationAeeePro(env, stageInput)

        assert script.filter_calls_with_arguments('bat', "call tini -useEnv:cdg.de ${env.autosar_tool} ${env.autosar_tool_version}").size() == 1
        assert script.filter_calls_with_arguments('bat', "call ${env.autosar_tool} -convertbcttoabacus -p ${env.bsw_repo} -m ${env.project_variant}").size() == 1
        assert script.filter_calls_with_arguments('bat', "call ${env.autosar_tool} -cdgb rebuild -p ${env.bsw_repo} -m ${env.project_variant}").size() == 1
    }
    @Test
    void testGenerateLibraries() {
        Map stageInput = [bsw_repo: "cubas-nxp-s32g"]
        m7Stages.generateLibraries(env, stageInput)

        assert script.filter_calls_with_arguments('bat', "cd ${env.bsw_repo}").size() == 1
        assert script.filter_calls_with_arguments('bat', "call Build.bat").size() == 1
    }
}