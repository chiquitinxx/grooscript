package org.grooscript.test

import org.grooscript.util.GrooScriptException
import org.grooscript.util.GsConsole

import javax.script.ScriptEngineManager
import javax.script.ScriptEngine
import javax.script.Bindings
import org.grooscript.util.Util

/**
 * JFL 27/08/12
 */
class TestJs {

    /**
     * Launch a js script and returns result binding
     * @param script to execute
     * @param map of start binding
     * @return map of bindings in result
     */
    static jsEval(script, map) {
        jsEval(script, map, null)
    }

    /**
     * Launch a js script and returns result binding
     * @param script to execute
     * @param map of start binding
     * @param jsFile grooscript.js can be null
     * @return map of bindings in result
     */
    static jsEval(script, map, jsFile) {
        def resultMap = [:]

        if (script) {
            try {

                String finalScript
                if (jsFile) {
                    finalScript = jsFile.text + script
                } else {
                    finalScript = addJsLibrarys(script)
                }

                //Load script manager
                ScriptEngineManager factory = new ScriptEngineManager()
                ScriptEngine engine = factory.getEngineByName('JavaScript')
                if (!engine) {
                    throw new GrooScriptException('Not engine available!')
                }
                Bindings bind = engine.createBindings()
                //Set the bindings
                if (map) {
                    map.each { bind.putAt(it.key, it.value) }
                }
                //Run javascript script
                try {
                    engine.eval(finalScript, bind)
                } catch (e) {
                    String message = e.message
                    GsConsole.error("Evaluation engine error (Lines: ${finalScript.readLines().size()}): ${message}")
                    if (message.contains('at line number')) {
                        def number = message.substring(message.indexOf('at line number') + 14) as int
                        def actualLine = number - 2
                        finalScript.readLines()[actualLine .. number + 2].each { line ->
                            println " ${actualLine++}: $line"
                        }
                        /*
                        finalScript.eachLine { line ->
                            if (actualLine in [number - 2, number + 2]) {
                                println " ${actualLine}: $line"
                            }
                            actualLine++
                        }*/
                    }
                    throw e
                }

                //Put binding result to resultMap
                if (bind) {
                    bind.each { resultMap.putAt(it.key, it.value) }
                }

                //Set assertFails var to the map
                resultMap.assertFails = resultMap.gSfails

            } catch (e) {
                throw new GrooScriptException("Fail in eval Js Script! - ${e.message}")
            }
        }
        resultMap
    }

    static addJsLibrarys(text) {
        def result = text
        //We get gscript functions file
        File file = Util.getJsFile('grooscript.js')
        //Add that file to javascript code
        result = file.text + result
        result
    }

    /**
     * Launch a js script
     * @param script
     * @return
     */
    static jsEval(script) {
        jsEval(script, null)
    }

    /**
     * Launch a js script
     * @param script
     * @param jsFile grooscript.js file
     * @return
     */
    static jsEvalWithFile(script, File jsFile) {
        jsEval(script, null, jsFile)
    }

    static File getGroovyTestScript(String name) {
        Util.getGroovyTestScriptFile(name)
    }
}
