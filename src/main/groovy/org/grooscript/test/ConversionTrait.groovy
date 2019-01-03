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

import org.grooscript.JsGenerator
import org.grooscript.GrooScript
import org.grooscript.convert.GsConverter
import org.grooscript.util.GsConsole

import static org.grooscript.util.Util.*

trait ConversionTrait {

    GsConverter converter = new GsConverter()
    NodeJs nodeJs = new NodeJs()

    /**
     * Read a groovy file and returns javascript conversion object
     * @param nameOfFile name of groovy file
     * @param jsResultOnConsole true if wanna println js result script
     * @param options for the GsConverter
     * @param textSearch in the js conversion script
     * @param textReplace replace searched text with this one
     */
    JsTestResult convertAndEvaluateWithJsEngine(nameOfFile, jsResultOnConsole = false, options = [:], textSearch = null, textReplace = null) {

        String jsScript = convertFile(nameOfFile, options)

        if (textSearch && jsScript.indexOf(textSearch) >= 0) {
            jsScript = jsScript.substring(0, jsScript.indexOf(textSearch)) +
                    textReplace + jsScript.substring(jsScript.indexOf(textSearch) + textSearch.size())
        }

        if (jsResultOnConsole) {
            GsConsole.message("jsScript Result->${LINE_SEPARATOR}$jsScript")
        }

        JavascriptEngine.jsEval(jsScript)
    }

    String convertFile(nameOfFile, options = null) {
        def file = JavascriptEngine.getGroovyTestScript(nameOfFile)
        converter.toJs(file.text, options)
    }

    boolean checkBuilderCodeAssertsFails(String code, jsResultOnConsole = false, options = [:]) {

        if (options) {
            options.each { key, value ->
                converter."$key" = value
            }
        }

        String jsScript = converter.toJs(code)

        def builderCode = GrooScript.convert(new File(JsGenerator.HTML_BUILDER_SOURCE).text)
        jsScript = builderCode + jsScript

        if (jsResultOnConsole) {
            GsConsole.message("jsScript Result->${LINE_SEPARATOR}$jsScript")
        }

        JavascriptEngine.jsEval(jsScript).assertFails
    }

    JsTestResult convertAndEvaluateWithNode(jsScript) {
        nodeJs.evaluate(jsScript)
    }

    boolean convertAndEvaluate(
            String fileName, jsResultOnConsole = false, options = [:], textSearch = null, textReplace = null) {
        def evaluationJsEngine =
                convertAndEvaluateWithJsEngine(fileName, jsResultOnConsole, options, textSearch, textReplace)
        if (evaluationJsEngine.assertFails) {
            println evaluationJsEngine.console
        }
        return !evaluationJsEngine.assertFails && !convertAndEvaluateWithNode(evaluationJsEngine.jsScript).assertFails
    }
}
