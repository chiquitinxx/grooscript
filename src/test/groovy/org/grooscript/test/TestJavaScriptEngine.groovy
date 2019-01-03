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
package org.grooscript.test

import javax.script.ScriptEngine
import javax.script.Bindings

class TestJavaScriptEngine extends GroovyTestCase implements ConversionTrait {

    private ScriptEngine engine

    void setUp() {
        engine = JavascriptEngine.javascriptEngine
    }

    def 'Basis script binding'() {
        Bindings bind = engine.createBindings()
        //Evaluates javascript code
        bind.put('a', a)
        bind.put('b', b)
        engine.eval('a="Hello "+a;b=b*5;', bind)

        expect:
        bind.get('a') == resultA
        bind.get('b') == resultB

        where:
        a       | resultA       | b   | resultB
        'Peter' | 'Hello Peter' | 0   | 0
        5       | 'Hello 5'     | 3   | 15
        5       | 'Hello 5'     | 'A' | Double.NaN

    }

    void testShortWayOfTesting() {

        JsTestResult result = JavascriptEngine.jsEval('a="Hello "+a;c=b*5;', [a: 'Jorge', b: 5])

        assert result.bind.c == 25
    }

    def 'function gSassert results'() {

        def map = JavascriptEngine.jsEval("gs.assert(${value});", null)

        expect:
        map.bind.gSfails == result

        where:
        value   | result
        'true'  | false
        'false' | true
        '1==2'  | true
    }

    void testSpeedJavascriptEngine() {
        def result = convertAndEvaluateWithJsEngine('TestSpeed')
        assert !result.assertFails
    }

    void testProblemsWithReservedWords() {
        assert !convertAndEvaluateWithJsEngine('ReservedWords').assertFails
    }
}
