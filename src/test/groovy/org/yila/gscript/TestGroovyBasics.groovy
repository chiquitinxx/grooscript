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
        //TODO
    }
}
