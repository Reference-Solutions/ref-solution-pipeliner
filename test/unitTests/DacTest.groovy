package unitTests

import com.refSolution.pipelinerdepot.utils.Dac
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.Assert



class DacTest {

    private ScriptEnvironment script
    private Dac dac
    private Map<String, String> env

    @Before
    void setUp() {
        
        script = new ScriptEnvironment(env)
        dac = new Dac(script, env)
    }

    @Test
    void testsphinxBuildDac() {
        env = [
        "docsPath" : "docs/dir",
        "ghFolder" : "Reference_solution.github.io",
        "docsurl" : "docs/url",
        "html_source_path" : "docs/dir",
        "html_destination_path" : "docs/dir"
        ]
        Map stageInput = [:]
        this.dac.sphinxBuildDac(env.docsPath)
        assert script.filter_calls_with_arguments('bat', "call ${env.docsPath}/make.bat clean").size() == 1
        assert script.filter_calls_with_arguments('bat', "call ${env.docsPath}/make.bat html").size() == 1
    }

    @Test
    void testghPublishDac() {
        env = [
        "docsPath" : "docs/dir",
        "docs_repo_name" : "Reference_solution.github.io",
        "docsurl" : "docs/url",
        "html_source_path" : "docs/dir",
        "html_destination_path" : "docs/dir"
        ]
        Map stageInput = [:]
        String docsPath = env.docsPath
        String docsurl = env.docsurl
        String ghFolder = env.docs_repo_name
        String html_source_path = env.html_source_path
        String html_destination_path = env.html_destination_path
        String dac_publish = stageInput.doc_publish
        this.dac.ghPublishDac(docsurl, ghFolder, html_source_path, html_destination_path)
        assert script.filter_calls_with_arguments('bat', "git clone ${docsurl}").size() == 1
        assert script.filter_calls_with_arguments('bat', "robocopy ${html_source_path} ${html_destination_path} /COPYALL /E").size() == 1
        assert script.filter_calls_with_arguments('bat', "cd ${ghFolder}").size() == 1

    }

}
