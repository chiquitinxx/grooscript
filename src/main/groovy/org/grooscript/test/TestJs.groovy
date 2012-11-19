package org.grooscript.test

import javax.script.ScriptEngineManager
import javax.script.ScriptEngine
import javax.script.Bindings
import org.grooscript.util.GsConsole
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
    static jsEval(script,map) {
        return jsEval(script,map,null)
    }

    /**
     * Launch a js script and returns result binding
     * @param script to execute
     * @param map of start binding
     * @param jsFile grooscript.js can be null
     * @return map of bindings in result
     */
    static jsEval(script,map,jsFile) {
        def resultMap = [:]

        //
        //println 'Ruta->'+System.getProperty('user.dir')
        if (script) {
            try {

                //def s = System.getProperty('path.separator')
                //File file = new File(System.getProperty('user.dir')+"src${s}main${s}resources${s}js${s}unused_gscript.js")

                def finalScript
                if (!jsFile) {
                    finalScript = addJsLibrarys(script)
                } else {
                    finalScript = jsFile.text + script
                }
                //println finalScript

                //Load script manager
                ScriptEngineManager factory = new ScriptEngineManager()
                ScriptEngine engine = factory.getEngineByName('JavaScript')
                if (!engine) {
                    throw new Exception('Not engine available!')
                }
                Bindings bind = engine.createBindings()
                //Set the bindings
                if (map) {
                    map.each {bind.putAt(it.key,it.value)}
                }
                //Run javascript script
                engine.eval(finalScript,bind)
                //Put binding result to resultMap
                if (bind) {
                    bind.each {resultMap.putAt(it.key,it.value)}
                }

                //Set assertFails var to the map
                resultMap.assertFails = resultMap.gSfails

            } catch (e) {
                GsConsole.error('TestJs.jsEval '+e.message)
                throw new Exception('Fail in eval Js Script! - '+e.message)
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
        //file = Util.getJsFile('grooscript.js')
        //Add that file to javascript code
        //result = file.text + result
        return result
    }

    /**
     * Launch a js script
     * @param script
     * @return
     */
    static jsEval(script) {
        jsEval(script,null)
    }

    /**
     * Launch a js script
     * @param script
     * @param jsFile grooscript.js file
     * @return
     */
    static jsEvalWithFile(script,File jsFile) {
        jsEval(script,null,jsFile)
    }

    static File getGroovyTestScript(String name) {
        Util.getGroovyTestScriptFile(name)
    }
}
