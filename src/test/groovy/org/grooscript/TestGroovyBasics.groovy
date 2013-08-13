package org.grooscript

import org.grooscript.test.ConversionMixin
import org.grooscript.test.TestJs
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
        def result =  TestJs.jsEval(jsScript)

        then:
        !result.assertFails

        when:
        jsScript = converter.toJs('assert false')
        result =  TestJs.jsEval(jsScript)

        then:
        result.assertFails
    }

    def 'test Web Main example' () {
        expect:
        !readAndConvert('webMainExample').assertFails
    }

    def 'variables and expressions'() {

        when:
        def result = readAndConvert('variablesAndExpressions')

        then:
        !result.assertFails
        result.b == 3.2
        result.a == 5
        result.c == 'Hello!'

    }

    def 'initial class'() {
        expect:
        !readAndConvert('initialClass').assertFails
    }

    def 'starting class stuff'() {
        when:
        def result = readAndConvert('startingClass')

        then:
        !result.assertFails
        result.gSconsole == 'Fan: GroovyRocks'
    }

    def 'starting closure stuff'() {
        expect:
        !readAndConvert('startingClosuresWithClasses').assertFails
    }

    def 'starting converting lists'() {
        expect:
        !readAndConvert('startingWorkOnLists').assertFails
    }

    def 'list functions'() {
        expect:
        !readAndConvert('listFunctions').assertFails
    }

    def 'some inheritance class'() {
        expect:
        !readAndConvert('someInheritance').assertFails
    }

    def 'maps and more closures'() {
        expect:
        !readAndConvert('mappingAndClosuring').assertFails
    }

    def 'control structures'() {
        expect:
        !readAndConvert('controlStructures').assertFails
    }

    def 'regular expressions'() {
        expect:
        !readAndConvert('regularExpressionsBegin').assertFails
    }

    def 'working with strings'() {
        expect:
        !readAndConvert('workingWithStrings').assertFails
    }

    def 'working with enums'() {
        expect:
        !readAndConvert('enums').assertFails
    }

    def 'static stuff in classes'() {
        expect:
        !readAndConvert('staticRealm').assertFails
    }

    def 'sets'() {
        expect:
        !readAndConvert('sets').assertFails
    }

    def 'functions and closures'() {
        expect:
        !readAndConvert('functions').assertFails
    }

    def 'interfaces'() {
        when: 'interface in code to convert'
        def result = readAndConvert('interfaces')

        then: 'is ignored'
        !result.assertFails
    }

    def 'test arithmetic'() {
        expect:
        !readAndConvert('arithmetic').assertFails
    }

}
