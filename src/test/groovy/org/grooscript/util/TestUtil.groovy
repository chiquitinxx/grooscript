/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grooscript.util

import org.grooscript.convert.NativeFunction
import spock.lang.Specification
import spock.lang.Unroll

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
        result == [new NativeFunction(className: 'A', methodName: 'a', code: 'NATIVE')]

        where:
        annotation                      | method                                     | post
        '@GsNative'                     | 'def a()'                                  | ''
        '@org.grooscript.asts.GsNative' | 'def a()'                                  | ''
        '@GsNative'                     | 'def static void a(data)'                  | ''
        '@GsNative'                     | 'private static void a(String data, args)' | ''
        '@GsNative'                     | 'private static void a(String data, args)' | 'return 1'
        '@GsNative'                     | 'void a()'                                 | 'doIt'
    }

    @Unroll
    def 'native functions with distinct containers'() {
        given:
        def text = """
${container} A {
    @GsNative
    def a() {
    /*
        NATIVE
    */}
}
"""

        when:
        def result = Util.getNativeFunctions(text)

        then:
        result == [new NativeFunction(className: 'A', methodName: 'a', code: 'NATIVE')]

        where:
        container << ['public class', 'public final class', 'class', 'trait', 'private trait']
    }

    @Unroll
    def 'native functions empty'() {
        expect:
        Util.getNativeFunctions(code) == []

        where:
        code << [null, '', 'println "hello"']
    }
}
