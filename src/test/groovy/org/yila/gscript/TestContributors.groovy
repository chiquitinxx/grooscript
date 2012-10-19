package org.yila.gscript

import spock.lang.Specification
import org.yila.gscript.test.TestJs

/**
 * First test for converts groovy code to javascript code
 * Following GroovyInAction Book
 * Chap 2. Groovy basics
 * JFL 27/08/12
 */
class TestContributors extends Specification {

    def converter = new GsConverter()

    def readAndConvert(nameOfFile,consoleOutput) {

        def file = TestJs.getGroovyTestScript(nameOfFile)

        def jsScript = converter.toJs(file.text)

        if (consoleOutput) {
            println 'jsScript->\n'+jsScript
        }

        return TestJs.jsEval(jsScript)
    }


    def 'test jochen' () {
        when:
        def result = readAndConvert('contribution/JochenTheodorou',false)

        then:
        !result.assertFails
    }

    def 'test MrHaki' () {
        expect:
        def result = readAndConvert(file,file=='contribution/MrHakiInject')
        //println 'Console->'+result.gSconsole
        !result.assertFails

        where:
        file                                |_
        'contribution/MrHakiClosureReturn'  |_
        'contribution/MrHakiFirstLast'      |_
        'contribution/MrHakiSum'            |_
        'contribution/MrHakiLooping'        |_
        'contribution/MrHakiInject'         |_
        'contribution/MrHakiGrep'           |_

    }

    def 'test alex anderson' () {
        when:
        def result = readAndConvert('contribution/AlexAnderson',false)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

}
