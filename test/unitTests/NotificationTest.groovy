package unitTests
 
import com.refSolution.pipelinerdepot.stages.CommonNotificationStages
import com.refSolution.pipelinerdepot.utils.Notification
import org.junit.Before
import org.junit.Test
import org.junit.Assert
 
class NotificationTest {
 
    private ScriptEnvironment script  
    private CommonNotificationStages commonNotificationStages
    private Map<String, String> env
    private Notification notification
 
    @Before
    void setUp() {
        env = [
              webhookurl: "https://bosch.webhook.office.com/webhookb2/15f2b8ac-ac44-4cf8-8ea7-15d9ac74af42@0ae51e19-07c8-4e4b-bb6d-648ee58410f4/JenkinsCI/a10ceccd01454db8b5ab28750cbc9056/7890af85-64c2-475d-b0d2-a5689d8799a1",
              color: "blue",
              from : "Refsolutions XSPACE (MS/PJ-MNT) <XSPACE.Refsolutions@in.bosch.com>",
              mimeType: "",
              subject: "Test Subject",
              to: "duraisamy.hariharan@in.bosch.com,r.thejeswarareddy@in.bosch.com,sreenathreddy.bandapalli@in.bosch.com,k.karthickraja@in.bosch.com,pooja.jitendrabhandari@in.bosch.com"
              ]
        script = new ScriptEnvironment(env)
        commonNotificationStages = new CommonNotificationStages(script, env)
        notification = new Notification(script, env)
        //office365ConnectorSend("Test webhook url", "Test message", "success", "green")

       
    }
 
 
    @Test
    void testTeamsNotification() {
        env = [webhookurl: "https//bosch.webhook.office.com/webhookb2/15f2b8ac-ac44-4cf8-8ea7-15d9ac74af42@0ae51e19-07c8-4e4b-bb6d-648ee58410f4/JenkinsCI/a10ceccd01454db8b5ab28750cbc9056/7890af85-64c2-475d-b0d2-a5689d8799a1",
        color: "green,red,gray",
        currentBuild: "SUCCESS",
        message: "Test message teams"
        ]
        Map stageInput = [:]
        String webhookUrl = "Test webhook url"
        String message =  "Test message"
        String status = "success"
        String color = "green"
        notification.sendTeams(webhookUrl, message, status, color)
   
 
        //assert script.filter_calls('stage', 'Team Notification').size() == 1
        
       
       
 
    }
 
    // @Test
    // void testBuildUser() {
    //     Map stageInput = [:]
    //     commonNotificationStages.getBuildUser(env, stageInput)
 
    //     assert script.filter_calls('Timer').size() == 1
       
 
    // }
}
 