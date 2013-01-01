package org.grooscript

import org.grooscript.test.TestJs
import spock.lang.Specification

/**
 * First test for converts groovy code to javascript code
 * Following GroovyInAction Book
 * Chap 2. Groovy basics
 * JFL 27/08/12
 */
class TestAdvanced extends Specification {

    def converter = new GsConverter()
    def static DEPENDENCY =  'Dependency'

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
        def result = readAndConvert('advanced/MysticTable',false)

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
		def result = readAndConvert('advanced/RegularExpressions',false)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'random world' () {
        when:
        def result = readAndConvert('advanced/RandomWorld',false)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'test Robot'() {
        when:
        def result = readAndConvert('advanced/SampleRobot',false)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

    /*
    def 'test Robot'() {
        when:
        def result = readAndConvert('robot/Robot',true)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

    def 'test robot'() {
        when:
        def result = readAndConvert('robot/RobotGame',true)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }
    */

    def 'closuring and maps again' () {
        when:
        def result = readAndConvert('advanced/ClosuringRevisited',false)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'sorting lists' () {
        when:
        def result = readAndConvert('advanced/Sorting',false)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

    def 'features 0.1' () {
        when:
        def result = readAndConvert('features/ZeroOne',false)

        then:
        !result.assertFails

    }


    def 'check dependency resolution'() {

        //def path = 'src/test/resources/dep/need'

        when:
        //This fails always on gradle
        GrooScript.setOwnClassPath('need')
        def String result = GrooScript.convert("class A {};def need = new Need()")

        then:
        result
        result.startsWith('function A()')
        result.endsWith('var need = Need();\n')
    }

    /*
    def 'check dependency resolution alone'() {

        when:
        //This fails always on gradle
        GrooScript.setOwnClassPath('need')
        def String result = GrooScript.convert("class B { def Need c}")

        then:
        result
        result.startsWith('function A()')
        result.endsWith('var need = Need();\n')
    }*/

    def 'mastering scope'() {
        when:
        def result = readAndConvert('advanced/MasterScoping',false)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'test setters'() {
        when:
        def result = readAndConvert('advanced/Setters',false)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

    def 'test getters'() {
        when:
        def result = readAndConvert('advanced/Getters',false)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

    def 'test getter and setters'() {
        when:
        def result = readAndConvert('advanced/GettersAndSetters',false)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'test missing method'() {
        when:
        def result = readAndConvert('advanced/MethodMissing',false)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'web example'() {
        when:
        def result = readAndConvert('advanced/WebExample',false)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'more string features'() {
        when:
        def result = readAndConvert('advanced/StringSecrets',true)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

}
