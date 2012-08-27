package org.yila.gscript.test

import spock.lang.Specification
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.Bindings
/**
 * Test Java 6 JavaScriptEngine and tests reduction code
 * JFL 27/08/12
 */
class TestJavaScriptEngine extends Specification {

    ScriptEngine engine

    def setup() {
        ScriptEngineManager factory = new ScriptEngineManager()
        engine = factory.getEngineByName('JavaScript')
    }

    def 'Basis script binding'() {
        Bindings bind = engine.createBindings()
        //Evaluates javascript code
        bind.put('a',a)
        bind.put('b',b)
        engine.eval('a="Hello "+a;b=b*5;',bind)

        expect:
        bind.get('a') == resultA
        bind.get('b') == resultB

        where:
        a       |resultA        |b      |resultB
        'Peter' |'Hello Peter'  |0      |0
        5       |'Hello 5'      |3      |15
        5       |'Hello 5'      |'A'    |Double.NaN

    }

    def 'short way for testing'() {

        def map = TestJs.jsEval('a="Hello "+a;c=b*5;',[a: 'Jorge',b: 5])

        expect:
        map.c==25
    }

}
