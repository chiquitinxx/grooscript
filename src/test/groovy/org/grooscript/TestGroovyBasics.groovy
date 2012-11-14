package org.grooscript

import org.grooscript.test.TestJs
import spock.lang.Specification

/**
 * First tests for converts groovy code to javascript code
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

    def readAndConvert(nameOfFile,consoleOutput) {

        def file = TestJs.getGroovyTestScript(nameOfFile)

        def jsScript = converter.toJs(file.text)

        if (consoleOutput) {
            println 'jsScript->\n'+jsScript
        }

        return TestJs.jsEval(jsScript)
    }

    def 'test Web Main example' () {
        when:
        def result = readAndConvert('webMainExample',false)

        then:
        !result.assertFails
    }

    def 'variables and expressions'() {

        when:
        def result = readAndConvert('variablesAndExpressions',false)

        then:
        !result.assertFails
        result.b == 3.2
        result.a == 5
        result.c == 'Hello!'

    }

    def 'inital class'() {
        when:
        def result = readAndConvert('initialClass',false)

        then:
        !result.assertFails
    }

    def 'starting class stuff'() {
        when:
        def result = readAndConvert('startingClass',false)

        then:
        !result.assertFails
        result.gSconsole == 'Fan: GroovyRocks'
    }

    def 'starting closure stuff'() {
        when:
        def result = readAndConvert('startingClosuresWithClasses',false)

        then:
        !result.assertFails
    }

    def 'starting converting lists'() {
        when:
        def result = readAndConvert('startingWorkOnLists',false)

        then:
        !result.assertFails
    }

    def 'list functions'() {
        when:
        def result = readAndConvert('listFunctions',false)

        then:
        !result.assertFails
    }

    def 'some inheritance class'() {
        when:
        def result = readAndConvert('someInheritance',false)

        then:
        !result.assertFails
    }

    def 'maps and more closures'() {
        when:
        def result = readAndConvert('mappingAndClosuring',false)

        then:
        !result.assertFails
    }

    def 'control structures'() {
        when:
        def result = readAndConvert('controlStructures',false)

        then:
        !result.assertFails
    }

    def 'regular expressions'() {
        when:
        def result = readAndConvert('regularExpressionsBegin',false)

        then:
        !result.assertFails
    }

    def 'working with strings'() {
        when:
        def result = readAndConvert('workingWithStrings',false)

        then:
        !result.assertFails
    }

    def 'working with enums'() {
        when:
        def result = readAndConvert('enums',false)

        then:
        !result.assertFails
    }

    def 'static stuff in classes'() {
        when:
        def result = readAndConvert('staticRealm',false)

        then:
        !result.assertFails
    }

}
