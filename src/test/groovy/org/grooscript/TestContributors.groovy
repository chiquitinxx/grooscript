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
        expect:
        !readAndConvert('contribution/JochenTheodorou').assertFails
    }

    @Unroll('Testing MrHaki #file')
    def 'test MrHaki' () {
        expect:
        !readAndConvert(file).assertFails

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
        expect:
        !readAndConvert('contribution/AlexAnderson').assertFails
    }

    def 'test mario garcia' () {
        expect:
        !readAndConvert('contribution/MarioGarcia').assertFails
    }

    @Unroll('Testing anonymous web #file')
    def 'test anonymous contributions in web' () {
        expect:
        def result = readAndConvert(file)
        !result.assertFails
        result.gSconsole.contains(text)

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
        !result.assertFails
    }

    def 'testing more web' () {
        expect:
        !readAndConvert('contribution/Anonymous3').assertFails
    }

    def 'testing mario extends'() {
        expect:
        !readAndConvert('contribution/MarioGarcia2').assertFails
    }

    def 'testing mario maps'() {
        expect:
        !readAndConvert('contribution/MarioGarcia3').assertFails
    }

    def 'twitter code found scoping closures'() {
        expect:
        !readAndConvert('contribution/Twitter1').assertFails
    }

    def 'myself'() {
        expect:
        !readAndConvert(file).assertFails

        where:
        file                       | _
        'contribution/MySelf1'     | _
    }

}
