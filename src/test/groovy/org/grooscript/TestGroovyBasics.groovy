package org.grooscript

import org.grooscript.test.ConversionMixin
import org.grooscript.test.TestJavascriptEngine
import spock.lang.Specification

/**
 * First tests for converts groovy code to javascript code
 * Following GroovyInAction Book
 * Chap 2. Groovy basics
 * JFL 27/08/12
 */
@Mixin([ConversionMixin])
class TestGroovyBasics extends Specification {

    def 'assert function'() {

        when:
        def jsScript = converter.toJs('assert true')
        //println 'jsScript->'+jsScript
        def result =  TestJavascriptEngine.jsEval(jsScript)

        then:
        !result.assertFails

        when:
        jsScript = converter.toJs('assert false')
        result =  TestJavascriptEngine.jsEval(jsScript)

        then:
        result.assertFails
    }

    def 'test Web Main example' () {
        expect:
        !convertAndEvaluate('webMainExample').assertFails
    }

    def 'variables and expressions'() {

        when:
        def result = convertAndEvaluate('variablesAndExpressions')

        then:
        !result.assertFails
        result.b == 3.2
        result.a == 5
        result.c == 'Hello!'

    }

    def 'initial class'() {
        expect:
        !convertAndEvaluate('initialClass').assertFails
    }

    def 'starting class stuff'() {
        when:
        def result = convertAndEvaluate('startingClass')

        then:
        !result.assertFails
        result.gSconsole == 'Fan: GroovyRocks'
    }

    def 'starting closure stuff'() {
        expect:
        !convertAndEvaluate('startingClosuresWithClasses').assertFails
    }

    def 'starting converting lists'() {
        expect:
        !convertAndEvaluate('startingWorkOnLists').assertFails
    }

    def 'list functions'() {
        expect:
        !convertAndEvaluate('listFunctions').assertFails
    }

    def 'some inheritance class'() {
        expect:
        !convertAndEvaluate('someInheritance').assertFails
    }

    def 'maps and more closures'() {
        expect:
        !convertAndEvaluate('mappingAndClosuring').assertFails
    }

    def 'control structures'() {
        expect:
        !convertAndEvaluate('controlStructures').assertFails
    }

    def 'regular expressions'() {
        expect:
        !convertAndEvaluate('regularExpressionsBegin').assertFails
    }

    def 'working with strings'() {
        expect:
        !convertAndEvaluate('workingWithStrings').assertFails
    }

    def 'working with enums'() {
        expect:
        !convertAndEvaluate('enums').assertFails
    }

    def 'static stuff in classes'() {
        expect:
        !convertAndEvaluate('staticRealm').assertFails
    }

    def 'sets'() {
        expect:
        !convertAndEvaluate('sets').assertFails
    }

    def 'functions and closures'() {
        expect:
        !convertAndEvaluate('functions').assertFails
    }

    def 'interfaces'() {
        when: 'interface in code to convert'
        def result = convertAndEvaluate('interfaces')

        then: 'is ignored'
        !result.assertFails
    }

    def 'test arithmetic'() {
        expect:
        !convertAndEvaluate('arithmetic').assertFails
    }
}
