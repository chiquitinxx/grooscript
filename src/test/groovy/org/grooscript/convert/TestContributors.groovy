/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grooscript.convert

import org.grooscript.test.ConversionMixin
import org.grooscript.test.JsTestResult
import org.grooscript.util.GrooScriptException
import spock.lang.Specification
import spock.lang.Unroll

@Mixin([ConversionMixin])
class TestContributors extends Specification {

    def 'test jochen' () {
        expect:
        convertAndEvaluate('contribution/JochenTheodorou')
    }

    @Unroll('Testing MrHaki #file')
    def 'test MrHaki' () {
        expect:
        convertAndEvaluate(file)

        where:
        file                                    | _
        'contribution/MrHakiClosureReturn'      | _
        'contribution/MrHakiFirstLast'          | _
        'contribution/MrHakiSum'                | _
        'contribution/MrHakiLooping'            | _
        'contribution/MrHakiInject'             | _
        'contribution/MrHakiGrep'               | _
        'contribution/MrHakiGetSetProperties'   | _
        'contribution/MrHakiSpread'             | _
        'contribution/MrHakiCategories'         | _
        'contribution/MrHakiTraits1'            | _
        'contribution/MrHakiTraits2'            | _
        'contribution/MrHakiCountList'          | _
        'contribution/MrHakiSwitch'             | _
        'contribution/MrHakiInit'               | _
    }

    def 'test alex anderson' () {
        expect:
        convertAndEvaluate('contribution/AlexAnderson')
    }

    def 'test mario garcia' () {
        expect:
        convertAndEvaluate('contribution/MarioGarcia')
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
        def result = convertAndEvaluateWithJsEngine('contribution/MonkFish', false, null,
                'gSobject.value = 0;',
                'gSobject.value = 0;gSobject.two = function() {return 2;};')

        then:
        !result.assertFails
    }

    def 'testing more web' () {
        expect:
        convertAndEvaluate('contribution/Anonymous3')
        convertAndEvaluate('contribution/Anonymous4')
        convertAndEvaluate('contribution/Anonymous5')
    }

    def 'testing mario extends'() {
        expect:
        convertAndEvaluate('contribution/MarioGarcia2')
    }

    def 'testing mario maps'() {
        expect:
        convertAndEvaluate('contribution/MarioGarcia3')
    }

    def 'twitter code found scoping closures'() {
        expect:
        convertAndEvaluate('contribution/Twitter1')
    }

    @Unroll('My test #file')
    def 'my tests and experiments'() {
        expect:
        convertAndEvaluate(file)

        where:
        file                       | _
        'contribution/MySelf1'     | _
        'contribution/MySelf2'     | _
        'contribution/MySelf3'     | _
        'contribution/MySelf4'     | _
        'contribution/MySelf5'     | _
        'contribution/MySelf6'     | _
        'contribution/MySelf7'     | _
        'contribution/MySelf8'     | _
        'contribution/MySelf9'     | _
        'contribution/MySelf10'    | _
        'contribution/MySelf11'    | _
        'contribution/MySelf12'    | _
        'contribution/MySelf13'    | _
        'contribution/MySelf14'    | _
        'contribution/MySelf15'    | _
        'contribution/MySelf16'    | _
        'contribution/MySelf17'    | _
        'contribution/MySelf18'    | _
        'contribution/MySelf19'    | _
    }

    @Unroll
    def 'guillaume example #file from talks'() {
        expect:
        convertAndEvaluate file

        where:
        file << [
                'contribution/Guillaume', 'contribution/GuillaumeClosuresComposition',
                'contribution/GuillaumeOptionalReturn', 'contribution/GuillaumeCommandChain',
                'contribution/GuillaumeTrailingClosure', 'contribution/GuillaumeCustomizeTruth'
        ]
    }

    def 'ronny is'() {
        expect:
        convertAndEvaluate('contribution/Ronny')
    }

    def 'mscharhag closure composition'() {
        expect:
        convertAndEvaluate('contribution/Mscharhag')
    }

    def 'jason winnebeck interface safe'() {
        expect:
        convertAndEvaluate('contribution/JasonWinnebeck')
    }

    def 'dinko assignation returns value assigned'() {
        expect:
        convertAndEvaluate('contribution/Dinko')
    }

    def 'menehtbeo found big bug'() {
        expect:
        convertAndEvaluate('contribution/Menehtbeo')
    }

    def 'yellowsnow list groupBy'() {
        expect:
        convertAndEvaluate('contribution/Yellowsnow')
    }

    def 'chrismil46 class stuff'() {
        expect:
        convertAndEvaluate('contribution/ChrisMiles')
    }

    def 'h1romas4 constructor field scope'() {
        expect:
        convertAndEvaluate('contribution/H1romas4')
    }

    def 'h1romas4 repeated GsNative'() {
        expect:
        convertAndEvaluate('contribution/H1romas4GsNative')
    }

    @Unroll
    def 'groovy site code fragment #number'() {
        expect:
        convertAndEvaluate("contribution/GroovySite${number}")

        where:
        number << [0, 1]
    }

    def 'Dilvan errors found'() {
        expect:
        convertAndEvaluate('contribution/Dilvan')
        convertAndEvaluate('contribution/DilvanEmpty')
        convertAndEvaluate('contribution/DilvanBreak')
    }

    def 'not supporting anonymous classes'() {
        when:
        convertAndEvaluate('contribution/DilvanInnerRunnable', true)

        then:
        def e = thrown(GrooScriptException)
        e.message == 'Compiler END ERROR on Script - Not supporting anonymous classes(java.lang.Runnable) ' +
                'in class contribution.WithRunnable'
    }
}
