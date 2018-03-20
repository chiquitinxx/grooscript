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
package org.grooscript.convert

import org.grooscript.GrooScript
import org.grooscript.test.JsTestResult
import org.grooscript.util.Util

class TestConvertBase extends GroovyTestCase {

    //Just started well
    def 'converter ready'() {
        when:
        def converter = new GsConverter()

        then:
        converter

        and: 'Returns null if no script passed'
        converter.toJs(null) == null
    }

    def 'conversion basic'() {
        when:
        def result = Util.fullProcessScript("println 'Trying GScript!'")

        then:
        result
        !result.assertFails
    }

    def 'full conversion results'() {
        when:
        JsTestResult result = Util.fullProcessScript("def a=0;println 'Hey';assert true")

        then:
        result.console == 'Hey'
        !result.assertFails
        result.bind.a == 0
        !result.exception
        result.jsScript == "var a = 0;${Util.LINE_SEPARATOR}gs.println(\"Hey\");${Util.LINE_SEPARATOR}" +
                "gs.assert(true, \"Assertion fails: true\");${Util.LINE_SEPARATOR}"
    }

    def 'use static class converter'() {
        when:
        def result = GrooScript.convert('def a=0')

        then:
        result == 'var a = 0;' + Util.LINE_SEPARATOR
    }
}
