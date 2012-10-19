package org.yila.gscript

import org.yila.gscript.test.TestJs
import spock.lang.Specification

/**
 * First test for converts groovy code to javascript code
 * Following GroovyInAction Book
 * Chap 2. Groovy basics
 * JFL 27/08/12
 */
class TestAdvanced extends Specification {

    def converter = new GsConverter()

    def readAndConvert(nameOfFile,consoleOutput) {

        def file = TestJs.getGroovyTestScript(nameOfFile)

        def jsScript = converter.toJs(file.text)

        if (consoleOutput) {
            println 'jsScript->\n'+jsScript
        }

        return TestJs.jsEval(jsScript)
    }


    def 'test tree object' () {
        when:
        def result = readAndConvert('advanced/Tree',false)

        then:
        !result.assertFails
    }

    def 'expando world' () {
        when:
        def result = readAndConvert('advanced/ExpandoWorld',false)

        then:
        !result.assertFails
    }

    def 'mystic table' () {
        when:
        def result = readAndConvert('advanced/MysticTable',true)

        then:
        !result.assertFails
    }

    def 'summer function callings' () {
        when:
        def result = readAndConvert('advanced/summer',false)

        then:
        !result.assertFails
    }

    def 'regular expressions' () {
        when:
        //TODO Continue here
		  def result = readAndConvert('advanced/RegularExpressions',true)

        then:
        println 'Console->'+result.gSconsole
        !result.assertFails
    }


}
