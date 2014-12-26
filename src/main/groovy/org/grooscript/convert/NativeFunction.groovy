package org.grooscript.convert

import groovy.transform.EqualsAndHashCode

/**
 * User: jorgefrancoleza
 * Date: 27/04/14
 */
@EqualsAndHashCode
class NativeFunction {
    String className
    String methodName
    String code

    String toString() {
        "${className} - ${methodName}"
    }
}
