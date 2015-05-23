package org.grooscript.convert.util

import groovy.transform.EqualsAndHashCode

/**
 * Created by jorgefrancoleza on 13/4/15.
 */
@EqualsAndHashCode
class RequireJsTemplate {

    String destinationFile
    String requireFolder
    List<RequireJsDependency> dependencies
    String jsCode
    List<String> classes
}

@EqualsAndHashCode
class RequireJsDependency {
    String path
    String name
}
