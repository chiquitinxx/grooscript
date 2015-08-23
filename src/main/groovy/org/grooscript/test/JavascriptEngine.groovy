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

import org.grooscript.util.GrooScriptException
import org.grooscript.util.GsConsole

import javax.script.ScriptEngineManager
import javax.script.ScriptEngine
import javax.script.Bindings
import org.grooscript.util.Util

class JavascriptEngine {

    /**
     * Launch a js script and returns result binding
     * @param script to execute
     * @param map of start binding
     * @return map of bindings in result
     */
    static JsTestResult jsEval(script, map) {
        jsEval(script, map, null)
    }

    /**
     * Launch a js script and returns result binding
     * @param script to execute
     * @param bindMap of start binding
     * @param jsFile grooscript.js can be null
     * @return map of bindings in result
     */
    static JsTestResult jsEval(script, bindMap, jsFile) {
        def testResult
        if (script) {
            String finalScript
            if (jsFile) {
                finalScript = jsFile.text + script
            } else {
                finalScript = addJsLibraries(script)
            }

            testResult = evaluateJsCode(finalScript, bindMap)
            testResult.jsScript = script
        }
        testResult
    }

    static String addJsLibraries(text) {
        //We get gscript functions file
        File file = Util.getJsFile('grooscript.js')
        //Add that file to javascript code
        def result = file.text + addEvaluationVars(text)
        result
    }

    static String addEvaluationVars(text) {
        def result = '\ngs.consoleOutput = false;\n' + text
        result = result + '\nvar gSfails = gs.fails;var gSconsole = gs.consoleData;\n'
        result
    }

    /**
     * Launch a js script
     * @param script
     * @return
     */
    static JsTestResult jsEval(script) {
        jsEval(script, null)
    }

    /**
     * Launch a js script
     * @param script
     * @param jsFile grooscript.js file
     * @return
     */
    static JsTestResult jsEvalWithFile(script, File jsFile) {
        jsEval(script, null, jsFile)
    }

    static File getGroovyTestScript(String name) {
        Util.getGroovyTestScriptFile(name)
    }

    static JsTestResult evaluateJsCode(String jsCode, bindMap = null) {
        def testResult = new JsTestResult()

        if (jsCode) {
            try {
                //Load script manager
                ScriptEngine engine = javascriptEngine
                Bindings bind = engine.createBindings()
                //Set the bindings
                if (bindMap) {
                    bindMap.each { bind.putAt(it.key, it.value) }
                }
                //Run javascript script
                try {
                    engine.eval(jsCode, bind)
                } catch (e) {
                    String message = e.message
                    GsConsole.error("Evaluation engine error (Lines: ${jsCode.readLines().size()}): ${message}")
                    if (message.contains('at line number')) {
                        def number = message.substring(message.indexOf('at line number') + 14) as int
                        if (number > 1) {
                            def actualLine = number - 2
                            jsCode.readLines()[actualLine .. number + 2].each { line ->
                                GsConsole.info " ${actualLine++}: $line"
                            }
                        }
                    }
                    throw e
                }

                //Put binding result to resultMap
                if (bind) {
                    bind.each { testResult.bind.putAt(it.key, it.value) }
                }
                testResult.jsScript = jsCode
                testResult.assertFails = testResult.bind.gSfails
                testResult.console = testResult.bind.gSconsole

            } catch (Throwable e) {
                throw new GrooScriptException("Fail evaluating Js Script! - ${e.message}")
            }
        }
        testResult
    }

    static ScriptEngine getJavascriptEngine() {
        ScriptEngineManager factory = new ScriptEngineManager()
        ScriptEngine engine = factory.getEngineByName('JavaScript')
        if (!engine) {
            throw new GrooScriptException('JavaScript engine not available!')
        }
        engine
    }
}
