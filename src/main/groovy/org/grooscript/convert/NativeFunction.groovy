package org.grooscript.convert

/**
 * User: jorgefrancoleza
 * Date: 27/04/14
 */
class NativeFunction {
    String className
    String methodName
    String code

    String toString() {
        "${className} - ${methodName}"
    }
}
