package org.yila.gscript

import org.yila.gscript.test.TestJs
import spock.lang.Specification

/**
 * First test for converts groovy code to javascript code
 * Following GroovyInAction Book
 * Chap 2. Groovy basics
 * JFL 27/08/12
 */
class TestGroovyBasics extends Specification {

    def converter = new GsConverter()

    def 'assert function'() {

        when:
        def jsScript = converter.toJs('assert true')
        //println 'jsScript->'+jsScript
        def result =  TestJs.jsEval(jsScript)

        then:
        !result.assertFails

        when:
        jsScript = converter.toJs('assert false')
        result =  TestJs.jsEval(jsScript)

        then:
        result.assertFails
    }

    def 'variables and expressions'() {

        when:
        def file = TestJs.getGroovyTestScript('variablesAndExpressions')
        def jsScript = converter.toJs(file.text)
        //println 'jsScript->\n'+jsScript
        def result =  TestJs.jsEval(jsScript)

        then:
        !result.assertFails
        result.b == 3.2
        result.a == 5
        result.c == 'Hello!'

    }

    def 'starting class stuff'() {
        when:
        def file = TestJs.getGroovyTestScript('startingClass')
        def jsScript = converter.toJs(file.text)
        //println 'jsScript->\n'+jsScript
        def result =  TestJs.jsEval(jsScript)

        then:
        !result.assertFails
        result.gSconsole == 'Fan: GroovyRocks'
    }

    def 'starting closure stuff'() {
        when:
        def file = TestJs.getGroovyTestScript('startingClosuresWithClasses')
        def jsScript = converter.toJs(file.text)
        println 'jsScript->\n'+jsScript
        def result =  TestJs.jsEval(jsScript)

        then:
        !result.assertFails
    }

    def 'converting lists'() {
        when:
        def file = TestJs.getGroovyTestScript('workOnLists')
        def jsScript = converter.toJs(file.text)
        //println 'jsScript->\n'+jsScript
        def result =  TestJs.jsEval(jsScript)

        then:
        !result.assertFails
    }
}
