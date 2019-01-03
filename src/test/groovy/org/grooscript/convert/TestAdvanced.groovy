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

class TestAdvanced extends GroovyTestCase implements ConversionTrait {

    void testTreeObject() {
        assert convertAndEvaluate('advanced/Tree')
    }

    void testExpandoWorld() {
        assert convertAndEvaluate('advanced/ExpandoWorld')
    }

    void testMysticTable() {
        assert convertAndEvaluate('advanced/MysticTable')
    }

    void testSummerFunctionCallings() {
        assert convertAndEvaluate('advanced/summer')
    }

    void testRegularExpressions() {
        assert convertAndEvaluate('advanced/RegularExpressions')
    }

    void testRandomWorld() {
        assert convertAndEvaluate('advanced/RandomWorld')
    }

    void testRobot() {
        assert convertAndEvaluate('advanced/SampleRobot')
    }

    void testClosuringAndMapsAgain() {
        assert convertAndEvaluate('advanced/ClosuringRevisited')
    }

    void testSortingLists() {
        assert convertAndEvaluate('advanced/Sorting')
    }

    void testFeaturesZeroOne() {
        assert convertAndEvaluate('features/ZeroOne')
    }

    void testMasteringScope() {
        assert convertAndEvaluate('advanced/MasterScoping')
    }

    void testSetters() {
        assert convertAndEvaluate('advanced/Setters')
    }

    void testGetters() {
        assert convertAndEvaluate('advanced/Getters')
    }

    void testGetterAndSetters() {
        assert convertAndEvaluate('advanced/GettersAndSetters')
    }

    void testMissingMethod() {
        assert convertAndEvaluate('advanced/MethodMissing')
    }

    void testWebExample() {
        assert convertAndEvaluate('advanced/WebExample')
    }

    void testAdvancedWebExample() {
        assert convertAndEvaluate('advanced/AdvancedWebExample')
    }

    void testStringFeatures() {
        assert convertAndEvaluate('advanced/StringSecrets')
    }

    void testObjectComparation() {
        assert convertAndEvaluate('advanced/Comparable')
    }

    void testListAndMapsFeatures() {
        assert convertAndEvaluate('advanced/ListMapsAdvanced')
    }

    void testGetPropertiesAndMethodsOfClasses() {
        assert convertAndEvaluate('advanced/PropertiesAndMethods')
    }

    void testGetTupleFromObject() {
        assert convertAndEvaluate('advanced/GetTupleFromObject')
    }

    void testMethodPointer() {
        assert convertAndEvaluate('advanced/MethodPointer')
    }

    void testSafeNavigation() {
        assert convertAndEvaluate('advanced/SafeNavigation')
    }

    void testListNinja() {
        assert convertAndEvaluate('advanced/ListNinja')
    }

    void testMaybeDsls() {
        assert convertAndEvaluate('advanced/TryDsls')
    }

    void testMultipleConditions() {
        assert convertAndEvaluate('advanced/MultipleConditions')
    }

    void testMethodMissingWithThis() {
        assert convertAndEvaluate('advanced/MethodMissingTwo')
    }

    void testCurryRcurryAndNcurry() {
        assert convertAndEvaluate('advanced/Curry')
    }

    void testMixinAnnotation() {
        assert convertAndEvaluate('advanced/MixinAst')
    }

    void testVariableNumberOfArguments() {
        assert convertAndEvaluate('advanced/VariableArguments')
    }

    void testRangesOfChars() {
        assert convertAndEvaluate('advanced/RangeChars')
    }

    void testReturnValues() {
        assert convertAndEvaluate('advanced/ReturnValues')
    }

    void testMethodPointerAdvanced() {
        assert convertAndEvaluate('advanced/MethodPointerAdvanced')
    }

    void testCallbackThis() {
        assert convertAndEvaluate('advanced/CallbackThis')
    }

    void testPropertyMissing() {
        assert convertAndEvaluate('advanced/PropertyMissing')
    }

    void testGetBooleanPropertiesWithIsFunctions() {
        assert convertAndEvaluate('advanced/BooleanProperties')
    }

    void testFinallyBlocksExecuted() {
        assert convertAndEvaluate('advanced/Finally')
    }

    void testSpreadMethodParams() {
        assert convertAndEvaluate('advanced/SpreadParams')
    }
}
