package org.yila.gscript

import org.yila.gscript.GsConverter
import spock.lang.Specification
import org.yila.gscript.test.TestJs

/**
 * First test for converts groovy code to javascript code
 * Following GroovyInAction Book
 * Chap 2. Groovy basics
 * JFL 27/08/12
 */
class TestConversionFails extends Specification {

    def converter = new GsConverter()

    def readAndConvert(nameOfFile,consoleOutput) {

        def file = TestJs.getGroovyTestScript(nameOfFile)

        def jsScript = converter.toJs(file.text)

        if (consoleOutput) {
            println 'jsScript->\n'+jsScript
        }

        return TestJs.jsEval(jsScript)
    }

    def 'test fail assertion' () {
        when:
        def result = readAndConvert('fail/assertFail',false)

        then:
        result.assertFails
        result.gSconsole == 'WOTT - false'
    }

    def 'test fail compile' () {
        when:
        def result = converter.toJs("a='Hello")

        then:
        Exception e = thrown()
        e.message == 'Compiler ERROR on Script'
    }

 }
