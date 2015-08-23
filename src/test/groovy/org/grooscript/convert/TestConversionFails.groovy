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

import org.grooscript.test.ConversionMixin
import org.grooscript.test.JsTestResult
import org.grooscript.util.Util
import spock.lang.Specification

@Mixin([ConversionMixin])
class TestConversionFails extends Specification {

    def 'test fail assertion' () {
        when:
        JsTestResult result = convertAndEvaluateWithJsEngine('fail/assertFail')

        then:
        result.assertFails
        result.console == "WOTT - false\nAssertion fails: (1 == 2) - false"
    }

    def 'test fail compile' () {
        when:
        converter.toJs("a='Hello")

        then:
        Exception e = thrown()
        e.message.startsWith 'Compiler ERROR on Script'
    }

    def 'access metaClass of groovy and java types not allowed'() {
        when:
        converter.toJs("String.metaClass.grita = {${Util.LINE_SEPARATOR}" +
                "    return delegate+'!'${Util.LINE_SEPARATOR}" +
                "}")

        then:
        Exception e = thrown()
        e.message.startsWith 'Compiler END ERROR on Script -Not allowed access metaClass'
    }
}
