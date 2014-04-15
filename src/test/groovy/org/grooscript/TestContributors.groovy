package org.grooscript

import org.grooscript.test.ConversionMixin
import spock.lang.Specification
import spock.lang.Unroll

/**
 * JFL 27/08/12
 */
@Mixin([ConversionMixin])
class TestContributors extends Specification {

    def 'test jochen' () {
        expect:
        !convertAndEvaluate('contribution/JochenTheodorou').assertFails
    }

    @Unroll('Testing MrHaki #file')
    def 'test MrHaki' () {
        expect:
        !convertAndEvaluate(file).assertFails

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
        'contribution/MrHakiCategories'         |_

    }

    def 'test alex anderson' () {
        expect:
        !convertAndEvaluate('contribution/AlexAnderson').assertFails
    }

    def 'test mario garcia' () {
        expect:
        !convertAndEvaluate('contribution/MarioGarcia').assertFails
    }

    @Unroll('Testing anonymous web #file')
    def 'test anonymous contributions in web' () {
        expect:
        def result = convertAndEvaluate(file)
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
        def result = convertAndEvaluate('contribution/MonkFish',false,null,
                'gSobject.value = 0;',
                'gSobject.value = 0;gSobject.two = function() {return 2;};')

        then:
        !result.assertFails
    }

    def 'testing more web' () {
        expect:
        !convertAndEvaluate('contribution/Anonymous3').assertFails
        !convertAndEvaluate('contribution/Anonymous4').assertFails
        !convertAndEvaluate('contribution/Anonymous5').assertFails
    }

    def 'testing mario extends'() {
        expect:
        !convertAndEvaluate('contribution/MarioGarcia2').assertFails
    }

    def 'testing mario maps'() {
        expect:
        !convertAndEvaluate('contribution/MarioGarcia3').assertFails
    }

    def 'twitter code found scoping closures'() {
        expect:
        !convertAndEvaluate('contribution/Twitter1').assertFails
    }

    def 'myself'() {
        expect:
        !convertAndEvaluate(file).assertFails

        where:
        file                       | _
        'contribution/MySelf1'     | _
        'contribution/MySelf2'     | _
    }

    def 'guillaume examples from talks'() {
        expect:
        !convertAndEvaluate('contribution/Guillaume').assertFails
        !convertAndEvaluate('contribution/GuillaumeClosuresComposition').assertFails
    }

    def 'ronny is'() {
        expect:
        !convertAndEvaluate('contribution/Ronny').assertFails
    }

    def 'mscharhag closure composition'() {
        expect:
        !convertAndEvaluate('contribution/Mscharhag').assertFails
    }
}
