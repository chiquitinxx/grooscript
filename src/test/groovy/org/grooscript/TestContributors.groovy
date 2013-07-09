package org.grooscript

import org.grooscript.test.ConversionMixin
import spock.lang.Specification
import org.grooscript.test.TestJs
import spock.lang.Unroll

/**
 * JFL 27/08/12
 */
@Mixin([ConversionMixin])
class TestContributors extends Specification {

    def 'test jochen' () {
        when:
        def result = readAndConvert('contribution/JochenTheodorou')

        then:
        !result.assertFails
    }

    @Unroll('Testing MrHaki #file')
    def 'test MrHaki' () {
        expect:
        def result = readAndConvert(file,false)//file=='contribution/MrHakiGetSetProperties')
        //println 'Console->'+result.gSconsole
        !result.assertFails

        where:
        file                                    |_
        'contribution/MrHakiClosureReturn'      |_
        'contribution/MrHakiFirstLast'          |_
        'contribution/MrHakiSum'                |_
        'contribution/MrHakiLooping'            |_
        'contribution/MrHakiInject'             |_
        'contribution/MrHakiGrep'               |_
        'contribution/MrHakiGetSetProperties'   |_
        'contribution/MrHakiSpread'             |_

    }

    def 'test alex anderson' () {
        when:
        def result = readAndConvert('contribution/AlexAnderson')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

    def 'test mario garcia' () {
        when:
        def result = readAndConvert('contribution/MarioGarcia')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

    @Unroll('Testing anonymous web #file')
    def 'test anonymous contributions in web' () {
        expect:
        def result = readAndConvert(file)
        !result.assertFails
        result.gSconsole.contains(text)
        //println result.gSconsole

        where:
        file                       | text
        'contribution/Anonymous0'  | 'FizzBuzz\n91'
        'contribution/Anonymous1'  | 'fizzbuzz\n91'
        'contribution/Anonymous2'  | 'fizZbuzZ\n16'

    }

    def 'bugs coming from monkfish'() {
        when:
        def result = readAndConvert('contribution/MonkFish',false,null,
                'gSobject.value = 0;',
                'gSobject.value = 0;gSobject.two = function() {return 2;};')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'testing more web' () {
        when:
        def result = readAndConvert('contribution/Anonymous3',false)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

    def 'testing mario extends'() {
        when:
        def result = readAndConvert('contribution/MarioGarcia2',false)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'testing mario maps'() {
        when:
        def result = readAndConvert('contribution/MarioGarcia3',false)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'twitter code found scoping closures'() {
        when:
        def result = readAndConvert('contribution/Twitter1',false)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'myself'() {
        when:
        def result = readAndConvert(file,true)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

        where:
        file                       | _
        'contribution/MySelf1'     | _
    }

}
