package org.grooscript

import org.grooscript.test.ConversionMixin
import spock.lang.Specification

/**
 * First test for converts groovy code to javascript code
 * Following GroovyInAction Book
 * Chap 2. Groovy basics
 * JFL 27/08/12
 */
@Mixin([ConversionMixin])
class TestAdvanced extends Specification {

    def 'test tree object' () {
        expect:
        convertAndEvaluate('advanced/Tree')
    }

    def 'expando world' () {
        expect:
        convertAndEvaluate('advanced/ExpandoWorld')
    }

    def 'mystic table' () {
        expect:
        convertAndEvaluate('advanced/MysticTable')
    }

    def 'summer function callings' () {
        expect:
        convertAndEvaluate('advanced/summer')
    }

    def 'regular expressions' () {
        expect:
        convertAndEvaluate('advanced/RegularExpressions')
    }

    def 'random world' () {
        expect:
        convertAndEvaluate('advanced/RandomWorld')
    }

    def 'test Robot'() {
        expect:
        convertAndEvaluate('advanced/SampleRobot')
    }

    def 'closuring and maps again' () {
        expect:
        convertAndEvaluate('advanced/ClosuringRevisited')
    }

    def 'sorting lists' () {
        expect:
        convertAndEvaluate('advanced/Sorting')
    }

    def 'features 0.1' () {
        expect:
        convertAndEvaluate('features/ZeroOne')
    }

    def 'mastering scope'() {
        expect:
        convertAndEvaluate('advanced/MasterScoping')
    }

    def 'test setters'() {
        expect:
        convertAndEvaluate('advanced/Setters')
    }

    def 'test getters'() {
        expect:
        convertAndEvaluate('advanced/Getters')
    }

    def 'test getter and setters'() {
        expect:
        convertAndEvaluate('advanced/GettersAndSetters')
    }

    def 'test missing method'() {
        expect:
        convertAndEvaluate('advanced/MethodMissing')
    }

    def 'web example'() {
        expect:
        convertAndEvaluate('advanced/WebExample')
    }

    def 'advanced web example'() {
        expect:
        convertAndEvaluate('advanced/AdvancedWebExample')
    }

    def 'more string features'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/StringSecrets').assertFails
    }

    def 'object comparation'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/Comparable').assertFails
    }

    def 'more list and maps features'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/ListMapsAdvanced').assertFails
    }

    def 'Get properties and methods of classes'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/PropertiesAndMethods').assertFails
    }

    def 'Get tuple from object'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/GetTupleFromObject').assertFails
    }

    def 'test method pointer'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/MethodPointer').assertFails
    }

    def 'test safe navigation'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/SafeNavigation').assertFails
    }

    def 'list ninja'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/ListNinja').assertFails
    }

    def 'maybe dsls'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/TryDsls').assertFails
    }

    def 'multiple conditions'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/MultipleConditions').assertFails
    }

    def 'method missing with this'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/MethodMissingTwo').assertFails
    }

    def 'curry, rcurry and ncurry'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/Curry').assertFails
    }

    def 'mixin annotation'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/MixinAst').assertFails
    }

    def 'variable number of arguments'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/VariableArguments').assertFails
    }

    def 'ranges of chars'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/RangeChars').assertFails
    }

    def 'return values'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/ReturnValues').assertFails
    }

    def 'test method pointer advanced'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/MethodPointerAdvanced').assertFails
    }

    def 'test callback this'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/CallbackThis').assertFails
    }

    def 'test property missing'() {
        expect:
        !convertAndEvaluateWithJsEngine('advanced/PropertyMissing').assertFails
    }
}
