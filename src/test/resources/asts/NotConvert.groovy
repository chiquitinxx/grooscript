package asts

import org.grooscript.asts.GsNotConvert

/**
 * JFL 10/11/12
 */

@GsNotConvert
class ClassNotConvert {
    def a
}

class A {
    @GsNotConvert
    def methodNotConvert() {
        println 'No!'
    }

    def hello() {
        println 'Hello!'
    }
}

assert true
