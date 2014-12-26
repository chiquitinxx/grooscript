package org.grooscript.util

import org.grooscript.convert.NativeFunction
import spock.lang.Specification
import spock.lang.Unroll

/**
 * User: jorgefrancoleza
 * Date: 05/04/14
 */
class TestUtil extends Specification {

    def 'test actual version'() {
        expect:
        Util.groovyVersionAtLeast('2.0')
    }

    @Unroll
    def 'native functions with code'() {
        given:
        def text = """
class A {
    ${annotation}
    ${method} {/*
        NATIVE
    */${post}
}
"""

        when:
        def result = Util.getNativeFunctions(text)

        then:
        result == [new NativeFunction(className: null, methodName: 'a', code: 'NATIVE')]

        where:
        annotation                      | method                                     | post
        '@GsNative'                     | 'def a()'                                  | ''
        '@org.grooscript.asts.GsNative' | 'def a()'                                  | ''
        '@GsNative'                     | 'def static void a(data)'                  | ''
        '@GsNative'                     | 'private static void a(String data, args)' | ''
        '@GsNative'                     | 'private static void a(String data, args)' | 'return 1'
        '@GsNative'                     | 'void a()'                                 | 'doIt'
    }
}
