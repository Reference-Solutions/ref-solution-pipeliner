package unitTests


// Mock for the scm object used by the checkout method


class ScriptEnvironment {

    // Save all calls and parameters for verifying behaviour
    public List<List<String>> callList = []
    public Map scm
    public Map<String, String> env


    ScriptEnvironment() {
        this.scm = setupSCMObject()
    }

    ScriptEnvironment(Map<String, String> env) {
        this.env = env
        this.scm = setupSCMObject()
    }
    void checkout(Map scm) {
        println("mock.checkout")
        def lst = new ArrayList<String>()
        lst.add("checkout")
        lst.add(scm.inspect())
        callList.add(lst)
    }
    
    def stage(String name, Closure clo) {
        println("mock.stage: " + name)
        def lst = new ArrayList<String>()
        lst.add("stage")
        lst.add(name)
        callList.add(lst)
        return clo.call()
    }
    
    void echo(String cmd) {
        println("mock.echo: " + cmd)
        def lst = new ArrayList<String>()
        lst.add("echo")
        lst.add(cmd)
        callList.add(lst)
    }

    String echo(Map map) {
        println("mock.echo: " + map.inspect())
        def lst = new ArrayList<String>()
        lst.add("echo")
        lst.add(map.inspect())
        callList.add(lst)

        return "stdout"
    }
    
    private Map setupSCMObject() {
        scm = [
            doGenerateSubmoduleConfigurations: false,
            submoduleCfg: [],
            branches: [],
            extensions: [],
            userRemoteConfigs: []
        ]

        return scm
    }

     /**
     * Mock of Jenkins sh with named parameters. Note return type void!
     *
     * Since named parameters change the output type, we should use
     * the wrappers in ScriptUtils instead.
     */
    void sh(String cmd) {
        println("mock.sh: " + cmd)
        def lst = new ArrayList<String>()
        lst.add("sh")
        lst.add(cmd)
        callList.add(lst)
    }

     /**
     * Mock of Jenkins bat with named parameters. Note return type void!
     *
     * Since named parameters change the output type, we should use
     * the wrappers in ScriptUtils instead.
     */
    void bat(String cmd) {
        println("mock.bat: " + cmd)
        def lst = new ArrayList<String>()
        lst.add("bat")
        lst.add(cmd)
        callList.add(lst)
    }

    
    /**
     * Helper method for inspecting specific calls.
     * @param call_filter String for e.g. "sh", "checkout", "junit" etc
     * @return Filtered ArrayList<ArrayList<String>>
     */
    ArrayList<ArrayList<String>> filter_calls(String call_filter) {
        return callList.findAll {f -> call_filter == f[0]}
    }

    /**
     * Helper method extending the functionality of filter_calls with additional
     * filtering on arguments.
     * @param call_filter
     * @param arg_filter
     * @return Filtered ArrayList<ArrayList<String>>
     */
    ArrayList<ArrayList<String>> filter_calls_with_arguments(String call_filter, String arg_filter) {
        return filter_call_list( filter_calls(call_filter), arg_filter )
    }

    /**
     * Helper method for filtering a call list
     * @param list ArrayList<ArrayList<String>>
     * @param filter String
     * @return Filtered ArrayList<ArrayList<String>>
     */
    ArrayList<ArrayList<String>> filter_call_list(ArrayList<ArrayList<String>> list, String filter) {
        return  list.findAll { args ->
            for (String arg : args) {
                if (arg.contains(filter))
                    return  true
            }
            return false
        }
    }
    def withCredentials(List credentials, Closure closure) {
        closure.call()
    }

    Map usernamePassword(Map map) {

    }

    Map usernameColonPassword(Map map) {

    }

}