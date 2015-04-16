package org.grooscript.convert.util

import groovy.transform.EqualsAndHashCode

/**
 * Created by jorgefrancoleza on 13/4/15.
 */
@EqualsAndHashCode
class RequireJsTemplate {

    String destinationFile
    String requireFolder
    List<String> dependencies
    String jsCode
    List<String> classes
}
