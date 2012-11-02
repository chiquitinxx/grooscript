package org.yila.gscript.test

import javax.script.ScriptEngineManager
import javax.script.ScriptEngine
import javax.script.Bindings
import org.yila.gscript.util.GsConsole
import org.yila.gscript.util.Util

/**
 * JFL 27/08/12
 */
class TestJs {

    /**
     * Launch a js script and returns result binding
     * @param script to execute
     * @param map of start bindingd
     * @return map of bindings in result
     */
    static jsEval(script,map) {
        def resultMap = [:]

        //
        //println 'Ruta->'+System.getProperty('user.dir')
        if (script) {
            try {

                //def s = System.getProperty('path.separator')
                //File file = new File(System.getProperty('user.dir')+"src${s}main${s}resources${s}js${s}gscript.js")

                def finalScript = addJsLibrarys(script)
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
        File file = Util.getJsFile('gscript.js')
        //Add that file to javascript code
        result = file.text + result
        file = Util.getJsFile('gsclass.js')
        //Add that file to javascript code
        result = file.text + result
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

    static File getGroovyTestScript(String name) {
        Util.getGroovyTestScriptFile(name)
    }
}
