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
package org.grooscript.asts

import org.grooscript.test.ConversionTrait
import org.grooscript.test.JavascriptEngine
import org.grooscript.util.Util

class TestAst extends GroovyTestCase implements ConversionTrait {

    def readAndConvert(nameOfFile,consoleOutput) {

        def file = JavascriptEngine.getGroovyTestScript(nameOfFile)
        def result = Util.fullProcessScript(file.text)

        if (consoleOutput) {
            println 'jsScript->' + Util.LINE_SEPARATOR + result.jsScript
        }
        if (result.exception) {
            assert false, 'Error: ' + result.exception
        }

        return result
    }

    def 'test GsNotConvert' () {
        when:
        def result = readAndConvert('asts/NotConvert', false)

        then:
        !result.assertFails
        result.jsScript.indexOf('NotConvert') < 0
    }

    def 'test simpleGsNative' () {
        expect:
        !readAndConvert('asts/simpleNative', false).assertFails
    }

    def 'test GsNative' () {
        when:
        def result = readAndConvert('asts/native', false)

        then:
        !result.assertFails
        result.jsScript.indexOf('return true;') > 0
    }

    def 'test advanced GsNative' () {
        expect:
        convertAndEvaluate('asts/advancedNative')
    }
}
