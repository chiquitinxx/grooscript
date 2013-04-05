package org.grooscript.ast

import org.grooscript.test.TestJs
import spock.lang.Specification
import org.grooscript.util.Util

/**
 * First test for converts groovy code to javascript code
 * Following GroovyInAction Book
 * Chap 2. Groovy basics
 * JFL 27/08/12
 */
class TestAst extends Specification {

    def readAndConvert(nameOfFile,consoleOutput) {

        def file = TestJs.getGroovyTestScript(nameOfFile)

        def result = Util.fullProcessScript(file.text)

        if (consoleOutput) {
            println 'jsScript->\n'+result.jsScript
        }

        return result
    }


    def 'test GsNotConvert' () {
        when:
        def result = readAndConvert('asts/NotConvert',false)

        then:
        !result.assertFails
        result.jsScript.indexOf('NotConvert')<0

    }

    def 'test simpleGsNative' () {
        when:
        def result = readAndConvert('asts/simpleNative',false)

        then:
        !result.assertFails

    }

    def 'test GsNative' () {
        when:
        def result = readAndConvert('asts/native',false)

        then:
        !result.assertFails
        result.jsScript.indexOf('return true;')>0

    }

}
