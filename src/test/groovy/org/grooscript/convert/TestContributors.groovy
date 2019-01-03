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

import org.grooscript.test.ConversionTrait
import org.grooscript.test.JsTestResult
import org.grooscript.util.GrooScriptException

class TestContributors extends GroovyTestCase implements ConversionTrait {

    void testJochen() {
        assert convertAndEvaluate('contribution/JochenTheodorou')
    }

    void testMrHaki() {
        assert convertAndEvaluate('contribution/MrHakiClosureReturn')
        assert convertAndEvaluate('contribution/MrHakiFirstLast')
        assert convertAndEvaluate('contribution/MrHakiSum')
        assert convertAndEvaluate('contribution/MrHakiLooping')
        assert convertAndEvaluate('contribution/MrHakiInject')
        assert convertAndEvaluate('contribution/MrHakiGrep')
        assert convertAndEvaluate('contribution/MrHakiGetSetProperties')
        assert convertAndEvaluate('contribution/MrHakiSpread')
        assert convertAndEvaluate('contribution/MrHakiCategories')
        assert convertAndEvaluate('contribution/MrHakiTraits1')
        assert convertAndEvaluate('contribution/MrHakiTraits2')
        assert convertAndEvaluate('contribution/MrHakiCountList')
        assert convertAndEvaluate('contribution/MrHakiSwitch')
        assert convertAndEvaluate('contribution/MrHakiInit')
    }

    void testAnonymousContributionsInWeb() {
        List resultText = ['FizzBuzz\n91', 'fizzbuzz\n91', 'fizZbuzZ\n16']
        0..2.each {
            JsTestResult result = convertAndEvaluateWithJsEngine('contribution/Anonymous' + it)
            assert !result.assertFails
            assert result.console.contains(resultText[it])
        }
    }

    void testMonkfishErrors() {
        JsTestResult result = convertAndEvaluateWithJsEngine('contribution/MonkFish', false, null,
                'gSobject.value = 0;',
                'gSobject.value = 0;gSobject.two = function() {return 2;};')

        assert !result.assertFails
    }

    void testMyExperiments() {
        1..19.each {
            assert convertAndEvaluate('contribution/MySelf' + it)
        }
    }

    void testGuillaumeExamples() {
        expect:
        assert convertAndEvaluate('contribution/Guillaume')
        assert convertAndEvaluate('contribution/GuillaumeClosuresComposition')
        assert convertAndEvaluate('contribution/GuillaumeOptionalReturn')
        assert convertAndEvaluate('contribution/GuillaumeCommandChain')
        assert convertAndEvaluate('contribution/GuillaumeTrailingClosure')
        assert convertAndEvaluate('contribution/GuillaumeCustomizeTruth')
    }

    void testContributions() {
        assert convertAndEvaluate('contribution/Ronny')
        assert convertAndEvaluate('contribution/Mscharhag')
        assert convertAndEvaluate('contribution/JasonWinnebeck')
        assert convertAndEvaluate('contribution/Dinko')
        assert convertAndEvaluate('contribution/Menehtbeo')
        assert convertAndEvaluate('contribution/Yellowsnow')
        assert convertAndEvaluate('contribution/ChrisMiles')
        assert convertAndEvaluate('contribution/H1romas4')
        assert convertAndEvaluate('contribution/H1romas4GsNative')
        assert convertAndEvaluate('contribution/AlexAnderson')
        assert convertAndEvaluate('contribution/MarioGarcia')
        assert convertAndEvaluate('contribution/Anonymous3')
        assert convertAndEvaluate('contribution/Anonymous4')
        assert convertAndEvaluate('contribution/Anonymous5')
        assert convertAndEvaluate('contribution/MarioGarcia2')
        assert convertAndEvaluate('contribution/MarioGarcia3')
        assert convertAndEvaluate('contribution/Twitter1')
    }

    void testGroovySiteCodeFragments() {
        assert convertAndEvaluate('contribution/GroovySite0')
        assert convertAndEvaluate('contribution/GroovySite1')
    }

    void testErrorsFoundByDilvan() {
        assert convertAndEvaluate('contribution/Dilvan')
        assert convertAndEvaluate('contribution/DilvanEmpty')
        assert convertAndEvaluate('contribution/DilvanBreak')
    }

    void testNotSupportAnonymousClasses() {
        try {
            convertAndEvaluate('contribution/DilvanInnerRunnable', true)
            fail('Must fail!')
        } catch (GrooScriptException exception) {
            assert exception.message == 'Compiler END ERROR on Script - Not supporting anonymous classes(java.lang.Runnable) ' +
                    'in class contribution.WithRunnable'
        }
    }
}
