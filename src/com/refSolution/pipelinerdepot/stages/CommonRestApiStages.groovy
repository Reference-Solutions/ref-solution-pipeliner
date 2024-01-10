
package com.refSolution.pipelinerdepot.stages

import com.bosch.pipeliner.LoggerDynamic
import com.refSolution.pipelinerdepot.utils.RestApi

/**
* Contains stages that can be reused across pipelines
*/
class CommonRestApiStages {

    private def script
    private Map env
    private LoggerDynamic logger
    private RestApi restapi

    /**
     * Constructor
     *
     * @param script Reference to the Jenkins scripted environment
     * @param env Map of Jenkins environment files
     */
    CommonRestApiStages(script, Map env) {
        this.script = script
        this.env = env
        this.restapi = new RestApi(script, env)
        this.logger = new LoggerDynamic(script)
    }


    def accessToken(Map env, Map stageInput = [:]){
        def task = "py python.py".execute()
        task.waitFor()
        println task.text
    }

    def getDesirtedStateByName(Map env, Map stageInput = [:]){
        script.stage("getDesirtedStateByName"){
            logger.info("getDesirtedStateByName")
            String tToken = stageInput.accessToken
            def command = """curl -X GET -H "Authorization: Bearer $tToken" https://ota.eu.bosch-mobility-cloud.com/api/applications/ota/desiredStates/Test2__AVH_app_test_install"""
            return sh(script: command, returnStdout: true).trim()
           
           
            // def command = 'curl -X GET -H "Authorization: Bearer '$tToken' " https://ota.eu.bosch-mobility-cloud.com/api/applications/ota/desiredStates/Test2__AVH_app_test_install'
            // return sh(script: command, returnStdout: true).trim()
            // def getCommand = ["curl", "-X", "GET", "-H", "Authorization: Bearer $tToken", "https://ota.eu.bosch-mobility-cloud.com/api/applications/ota/desiredStates/Test2__AVH_app_test_install"]
            // //def getCommand = 'curl -X GET -H "Authorization: Bearer ${tToken" https://ota.eu.bosch-mobility-cloud.com/api/applications/ota/desiredStates/Test2__AVH_app_test_install'
            // def generatedResponse = getCommand.execute().text
            // logger.info("getDesirtedStateByName executed")
            // //def generatedResponse = command.execute().text
            // //logger.info(generatedResponse)
            // def expectedResponse = '{"name":"T2k__AVH_app_test_install","specification":{"domains":[{"id":"avh_test","components":[{"id":"avh_app_test","version":"1.0","config":[{"key":"image","value":"https://api.devices.eu.bosch-mobility-cloud.com/v3/device/blobs/app_avh_install_v1.0.swpkg?token=0727673d-ddb9-4884-9142-f89d6d318921"}]}],"config":[{"key":"image-avh-app-test","value":"https://api.devices.eu.bosch-mobility-cloud.com/v3/device/blobs/vehiclepkg_app_avh_install_v1.0.tar/?token=ef199377-684c-4195-b2d0-2f3aa5857f24"}]}],"baselines":[{"components":["avh:app_demo"],"title":"avh-app-test_k"}]},"createdOn":"2024-01-03T09:44:46.917Z","lastUpdatedOn":"2024-01-03T09:44:46.917Z","_links":{"self":{"href":"https://ota.eu.bosch-mobility-cloud.com/api/applications/ota/desiredStates/Test2__AVH_app_test_install"}}}'
            // //readDesiredState(expectedResponse, generatedResponse)
        }
        
    }

    def readDesiredState(String expectedJSON, String actualJSON) {
        def jsonSlurper = new groovy.json.JsonSlurper()
        def expected = jsonSlurper.parseText(expectedJSON)
        def actual = jsonSlurper.parseText(actualJSON)
    
        if (expected == actual) {
            println("The JSON responses are same.")
        } else {
    
            println("The JSON responses are different.")
            //println("Expected JSON: $expected")
            //println("Actual JSON: $actual")
    
            def jsonFile = 'C:/Users/wwt1cob/DesiredStateJsonData.json'
            def accessToken = 'eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJwX3ZSenRWT3NRWlB4THp4bDdxQkViUUthMFVoSml5bkxLRW1ZWnJ6OTBZIn0.eyJleHAiOjE3MDQ4MjE0NjYsImlhdCI6MTcwNDgyMDg2NiwiYXV0aF90aW1lIjoxNzA0Nzk5OTY5LCJqdGkiOiJjYTczNGY5Ny05MTA2LTQxOTktYjdjNS1lODI5YjUyYjRjMjciLCJpc3MiOiJodHRwczovL3AyLmF1dGh6LmJvc2NoLmNvbS9hdXRoL3JlYWxtcy9FVV9SQl9GTEVBVEVTVCIsImF1ZCI6WyJyb2xlcy1tZXJjdXIiLCJyb2xlcy1iZXRzIiwicm9sZXMtYmUxIl0sInN1YiI6IjY2ODkzMGEyLWE3NmYtNGU2NS1hOGFmLWIwODY1NWFlYjFiMyIsInR5cCI6IkJlYXJlciIsImF6cCI6InBvc3RtYW4taW50ZXJuYWwiLCJzZXNzaW9uX3N0YXRlIjoiNDJlNzE1MzgtNmNiNS00YzhmLTgxNmEtM2NlNjQ2YjFiZjllIiwicmVzb3VyY2VfYWNjZXNzIjp7InJvbGVzLW1lcmN1ciI6eyJyb2xlcyI6WyJGT1RBX0FTU0lHTk1FTlRfQ1JFQVRFIiwiRk9UQV9VUERBVEVfUEFDS0FHRV9SRUFEIiwiVk1TX0FTU0lHTk1FTlRfU0lHTk9GRiIsIlZNU19DQU1QQUlHTl9ERUxFVEUiLCJGT1RBX1ZFSElDTEVfRUNVX0FTU0lHTk1FTlRfUkVBRCIsIkZvdGFPcGVyYXRvciIsIkZPVEFfQVNTSUdOTUVOVF9VUERBVEUiLCJWTVNfQ0FNUEFJR05fUkVBRCIsIkZPVEFfRElTVFJJQlVUSU9OX1BBQ0tBR0VfUkVBRCIsIkZPVEFfVVBEQVRFX1BBQ0tBR0VfREVMRVRFIiwiRk9UQV9ESVNUUklCVVRJT05fUEFDS0FHRV9VUERBVEUiLCJGT1RBX0FTU0lHTk1FTlRfREVMRVRFIiwiVk1TX0NBTVBBSUdOX0NSRUFURSIsIkZPVEFfQVNTSUdOTUVOVF9SRUFEIiwiRk9UQV9VUERBVEVfUEFDS0FHRV9VUERBVEUiLCJGT1RBX0RJU1RSSUJVVElPTl9QQUNLQUdFX0RFTEVURSIsIkZPVEFfRElTVFJJQlVUSU9OX1BBQ0tBR0VfQ1JFQVRFIiwiRk9UQV9VUERBVEVfUEFDS0FHRV9DUkVBVEUiXX0sInJvbGVzLWJldHMiOnsicm9sZXMiOlsiU01fQ09ORklHVVJFUiIsIk9GQ19WU0NfREVGSU5JVElPTl9QUk9WSURFUiIsIk9GQ19DQUxMRVIiLCJEUF9URUNIX0RFVklDRV9TWU5DSFJPTklaRVIiLCJTTV9NQVBQRVIiXX0sInJvbGVzLWJlMSI6eyJyb2xlcyI6WyJGTV9DSEFOR0VfQUxMIiwiUk1fQ0hBTkdFX0FMTCIsIkRDX0NIQU5HRV9BTEwiLCJSRF9SRUFEX0FMTCIsIlJHX1JFQURfQUxMIiwiUkRfQ0hBTkdFX0FMTCIsIkRQX1JFQURfQUxMIiwiRk1fUkVBRF9BTEwiLCJEQ19SRUFEX0FMTCIsIlJHX0NIQU5HRV9BTEwiLCJSTV9NQ0RDT1JFX0FMTCIsIlJNX1JFQURfQUxMIiwiQUNDRVNTIiwiRFBfQ0hBTkdFX0FMTCJdfX0sInNjb3BlIjoib3BlbmlkIHJlbW90ZS1tZWFzdXJlbWVudCB2bXMtaW52ZW50b3J5LXNlcnZpY2UgbWNkLWNvcmUgdm1zLWNhbXBhaWduLXNlcnZpY2Ugc3lzdGVtIHJlbW90ZS1kaWFnbm9zdGljcyBkZXZpY2UtY29tbXVuaWNhdGlvbiBkZXZpY2UtcHJvdmlzaW9uaW5nIGZsZWV0LW1hbmFnZW1lbnQgc2VsZi1tYXBwaW5nIGRhdGFwb2ludHMtc2VydmljZSBvdGEtZnVuY3Rpb24tY2FsbHMgZm90YS1kaXN0cmlidXRpb24iLCJzaWQiOiI0MmU3MTUzOC02Y2I1LTRjOGYtODE2YS0zY2U2NDZiMWJmOWUiLCJ0aWQiOiJFVV9SQl9GTEVBVEVTVCJ9.Ia9HThxGIyBAxocMVHOF8SDNd27wqnKO_Ry4zsfDxpySuM1rPol1E62g6o4n4p2Zu56tBOUekhc7SdMgvS0m4hJLUmom3CXqR0fhFVsJuHFw1peFnq7-as6fvX_nwpOCtoF8czOOuvx1dCOuuwbY0mTlC-wy5AHumE96mIq0aHy54pB5iuegEM1-JjZz_wCEI5S-K-s29HxDI4fh-QThR2hgMIVsgVNMfECjpDZK5S12KYdDwigP1YZcFK2YreKKs6ljW1rEpjepWgWgbbsE_ToPBgeIMbKuFlGMW--7iM-4oQZCO8ThAFv-T62RV5JRXHAMFwKEmSWEgCz_85ubrw'
            def apiUrl = 'https://ota.eu.bosch-mobility-cloud.com/api/applications/ota/desiredStates'
        
            //writeDesiredState(jsonFile,accessToken,apiUrl)
        }
}


    def method2(Map env, Map stageInput = [:]){
        script.stage("method2 stage"){
            logger.info("method2")
        }
        
    
    }
}
