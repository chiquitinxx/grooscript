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
        when:
        def result = readAndConvert('webMainExample')

        then:
        !result.assertFails
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
        when:
        def result = readAndConvert('initialClass')

        then:
        !result.assertFails
    }

    def 'starting class stuff'() {
        when:
        def result = readAndConvert('startingClass')

        then:
        !result.assertFails
        result.gSconsole == 'Fan: GroovyRocks'
    }

    def 'starting closure stuff'() {
        when:
        def result = readAndConvert('startingClosuresWithClasses')

        then:
        !result.assertFails
    }

    def 'starting converting lists'() {
        when:
        def result = readAndConvert('startingWorkOnLists')

        then:
        !result.assertFails
    }

    def 'list functions'() {
        when:
        def result = readAndConvert('listFunctions')

        then:
        !result.assertFails
    }

    def 'some inheritance class'() {
        when:
        def result = readAndConvert('someInheritance')

        then:
        !result.assertFails
    }

    def 'maps and more closures'() {
        when:
        def result = readAndConvert('mappingAndClosuring')

        then:
        !result.assertFails
    }

    def 'control structures'() {
        when:
        def result = readAndConvert('controlStructures')

        then:
        !result.assertFails
    }

    def 'regular expressions'() {
        when:
        def result = readAndConvert('regularExpressionsBegin')

        then:
        !result.assertFails
    }

    def 'working with strings'() {
        when:
        def result = readAndConvert('workingWithStrings')

        then:
        !result.assertFails
    }

    def 'working with enums'() {
        when:
        def result = readAndConvert('enums')

        then:
        !result.assertFails
    }

    def 'static stuff in classes'() {
        when:
        def result = readAndConvert('staticRealm')

        then:
        !result.assertFails
    }

    def 'sets'() {
        when:
        def result = readAndConvert('sets')

        then:
        !result.assertFails
    }

    def 'functions and closures'() {
        when:
        def result = readAndConvert('functions')

        then:
        //println result
        !result.assertFails
    }

}
