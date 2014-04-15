package org.grooscript

import org.grooscript.test.ConversionMixin
import org.grooscript.test.JsTestResult
import spock.lang.Specification
import spock.lang.Unroll

/**
 * JFL 27/08/12
 */
@Mixin([ConversionMixin])
class TestContributors extends Specification {

    def 'test jochen' () {
        expect:
        !convertAndEvaluateWithJsEngine('contribution/JochenTheodorou').assertFails
    }

    @Unroll('Testing MrHaki #file')
    def 'test MrHaki' () {
        expect:
        !convertAndEvaluateWithJsEngine(file).assertFails

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
        !convertAndEvaluateWithJsEngine('contribution/AlexAnderson').assertFails
    }

    def 'test mario garcia' () {
        expect:
        !convertAndEvaluateWithJsEngine('contribution/MarioGarcia').assertFails
    }

    @Unroll('Testing anonymous web #file')
    def 'test anonymous contributions in web' () {
        expect:
        JsTestResult result = convertAndEvaluateWithJsEngine(file)
        !result.assertFails
        result.console.contains(text)

        where:
        file                       | text
        'contribution/Anonymous0'  | 'FizzBuzz\n91'
        'contribution/Anonymous1'  | 'fizzbuzz\n91'
        'contribution/Anonymous2'  | 'fizZbuzZ\n16'
    }

    def 'bugs coming from monkfish'() {
        when:
        def result = convertAndEvaluateWithJsEngine('contribution/MonkFish',false,null,
                'gSobject.value = 0;',
                'gSobject.value = 0;gSobject.two = function() {return 2;};')

        then:
        !result.assertFails
    }

    def 'testing more web' () {
        expect:
        !convertAndEvaluateWithJsEngine('contribution/Anonymous3').assertFails
        !convertAndEvaluateWithJsEngine('contribution/Anonymous4').assertFails
        !convertAndEvaluateWithJsEngine('contribution/Anonymous5').assertFails
    }

    def 'testing mario extends'() {
        expect:
        !convertAndEvaluateWithJsEngine('contribution/MarioGarcia2').assertFails
    }

    def 'testing mario maps'() {
        expect:
        !convertAndEvaluateWithJsEngine('contribution/MarioGarcia3').assertFails
    }

    def 'twitter code found scoping closures'() {
        expect:
        !convertAndEvaluateWithJsEngine('contribution/Twitter1').assertFails
    }

    def 'myself'() {
        expect:
        !convertAndEvaluateWithJsEngine(file).assertFails

        where:
        file                       | _
        'contribution/MySelf1'     | _
        'contribution/MySelf2'     | _
    }

    def 'guillaume examples from talks'() {
        expect:
        !convertAndEvaluateWithJsEngine('contribution/Guillaume').assertFails
        !convertAndEvaluateWithJsEngine('contribution/GuillaumeClosuresComposition').assertFails
    }

    def 'ronny is'() {
        expect:
        !convertAndEvaluateWithJsEngine('contribution/Ronny').assertFails
    }

    def 'mscharhag closure composition'() {
        expect:
        !convertAndEvaluateWithJsEngine('contribution/Mscharhag').assertFails
    }
}
