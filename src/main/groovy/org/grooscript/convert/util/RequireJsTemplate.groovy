package org.grooscript.convert.util

import groovy.transform.Canonical

/**
 * Created by jorgefrancoleza on 13/4/15.
 */
@Canonical
class RequireJsTemplate {

    String destinationFile
    String requireFolder
    List<RequireJsDependency> dependencies
    String jsCode
    List<String> classes
}

@Canonical
class RequireJsDependency {
    String path
    String name
}
