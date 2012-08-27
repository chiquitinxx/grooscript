package org.yila.gscript.test

import javax.script.ScriptEngineManager
import javax.script.ScriptEngine
import javax.script.Bindings
import org.yila.gscript.util.GsConsole

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
        if (script) {
            try {
                ScriptEngineManager factory = new ScriptEngineManager()
                ScriptEngine engine = factory.getEngineByName('JavaScript')
                Bindings bind = engine.createBindings()
                if (map) {
                    map.each {bind.putAt(it.key,it.value)}
                }
                engine.eval(script,bind)
                if (bind) {
                    bind.each {resultMap.putAt(it.key,it.value)}
                }
            } catch (e) {
                GsConsole.error('TestJs.jsEval '+e.message)
            }
        }
        resultMap
    }
}
