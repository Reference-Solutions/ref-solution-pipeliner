package unitTests

/**
 * Mock class for Artifactory related data structure BuildInfo
 */
class BuildInfo {

    public BuildInfo() {
    }

}

/**
 * Mock class for Artifactory
 */
class Artifactory {

    private String credentialsId
    private boolean bypassProxy

    public Artifactory() {
    }

    Artifactory newServer(Map map) {
        return this
    }
    Artifactory server(String name) {
        return this
    }

    BuildInfo upload(String uploadSpec) {
        return new BuildInfo()
    }

    BuildInfo download(String downloadSpec) {
        return new BuildInfo()
    }

    BuildInfo newBuildInfo() {
        return new BuildInfo()
    }

    def publishBuildInfo(BuildInfo buildInfo) {
    }

    String getUrl() {
        return ""
    }

}
