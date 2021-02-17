package common

import java.nio.file.NoSuchFileException
import groovy.json.JsonSlurper

class RepoHandler {
    private final def context
    private final Map configs
    private final Map ADOPT_DEFAULTS_JSON
    private Map USER_DEFAULTS_JSON

    private final String ADOPT_JENKINS_DEFAULTS_URL = "https://raw.githubusercontent.com/AdoptOpenJDK/ci-jenkins-pipelines/master/pipelines/defaults.json"

    /*
    Constructor
    */
    RepoHandler (def context, Map<String, ?> configs) {
        this.context = context
        this.configs = configs

        def getAdopt = new URL(ADOPT_JENKINS_DEFAULTS_URL).openConnection()
        this.ADOPT_DEFAULTS_JSON = new JsonSlurper().parseText(getAdopt.getInputStream().getText()) as Map
    }

    /*
    Getter to retrieve user's git remote config
    */
    public Map<String, ?> getUserRemoteConfigs() {
        return configs
    }

    /*
    Getter to retrieve adopt's defaults
    */
    public Map<String, ?> getAdoptDefaultsJson() {
        return ADOPT_DEFAULTS_JSON
    }

    /*
    Getter to retrieve user's defaults
    */
    public Map<String, ?> getUserDefaultsJson() {
        return USER_DEFAULTS_JSON
    }

    /*
    Setter to retrieve and save a user defaults json inside the object
    */
    public Map<String, ?> setUserDefaultsJson(String url) {
        def getUser = new URL(url).openConnection()
        this.USER_DEFAULTS_JSON = new JsonSlurper().parseText(getUser.getInputStream().getText()) as Map
    }

    /*
    Changes dir to Adopt's build script repo
    */
    public void checkoutBuildAdopt () {
        context.checkout([$class: 'GitSCM',
            branches: [ [ name: ADOPT_DEFAULTS_JSON["repository"]["branch"] ] ],
            userRemoteConfigs: [ [ url: ADOPT_DEFAULTS_JSON["repository"]["build_url"] ] ]
        ])
    }

    /*
    Changes dir to Adopt's jenkins script repo
    */
    public void checkoutJenkinsAdopt () {
        context.checkout([$class: 'GitSCM',
            branches: [ [ name: ADOPT_DEFAULTS_JSON["repository"]["branch"] ] ],
            userRemoteConfigs: [ [ url: ADOPT_DEFAULTS_JSON["repository"]["pipeline_url"] ] ]
        ])
    }

    /*
    Changes dir to the user's repo
    */
    public void checkoutUser () {
        context.checkout([$class: 'GitSCM',
            branches: [ [ name: configs["branch"] ] ],
            userRemoteConfigs: [ configs["remotes"] ]
        ])
    }

}