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
        !convertAndEvaluate('advanced/Tree').assertFails
    }

    def 'expando world' () {
        expect:
        !convertAndEvaluate('advanced/ExpandoWorld').assertFails
    }

    def 'mystic table' () {
        expect:
        !convertAndEvaluate('advanced/MysticTable').assertFails
    }

    def 'summer function callings' () {
        expect:
        !convertAndEvaluate('advanced/summer').assertFails
    }

    def 'regular expressions' () {
        expect:
        !convertAndEvaluate('advanced/RegularExpressions').assertFails
    }

    def 'random world' () {
        expect:
        !convertAndEvaluate('advanced/RandomWorld').assertFails
    }

    def 'test Robot'() {
        expect:
        !convertAndEvaluate('advanced/SampleRobot').assertFails
    }

    def 'closuring and maps again' () {
        expect:
        !convertAndEvaluate('advanced/ClosuringRevisited').assertFails
    }

    def 'sorting lists' () {
        expect:
        !convertAndEvaluate('advanced/Sorting').assertFails
    }

    def 'features 0.1' () {
        expect:
        !convertAndEvaluate('features/ZeroOne').assertFails
    }

    def 'mastering scope'() {
        expect:
        !convertAndEvaluate('advanced/MasterScoping').assertFails
    }

    def 'test setters'() {
        expect:
        !convertAndEvaluate('advanced/Setters').assertFails
    }

    def 'test getters'() {
        expect:
        !convertAndEvaluate('advanced/Getters').assertFails
    }

    def 'test getter and setters'() {
        expect:
        !convertAndEvaluate('advanced/GettersAndSetters').assertFails
    }

    def 'test missing method'() {
        expect:
        !convertAndEvaluate('advanced/MethodMissing').assertFails
    }

    def 'web example'() {
        expect:
        !convertAndEvaluate('advanced/WebExample').assertFails
    }

    def 'advanced web example'() {
        expect:
        !convertAndEvaluate('advanced/AdvancedWebExample').assertFails
    }

    def 'more string features'() {
        expect:
        !convertAndEvaluate('advanced/StringSecrets').assertFails
    }

    def 'object comparation'() {
        expect:
        !convertAndEvaluate('advanced/Comparable').assertFails
    }

    def 'more list and maps features'() {
        expect:
        !convertAndEvaluate('advanced/ListMapsAdvanced').assertFails
    }

    def 'Get properties and methods of classes'() {
        expect:
        !convertAndEvaluate('advanced/PropertiesAndMethods').assertFails
    }

    def 'Get tuple from object'() {
        expect:
        !convertAndEvaluate('advanced/GetTupleFromObject').assertFails
    }

    def 'test method pointer'() {
        expect:
        !convertAndEvaluate('advanced/MethodPointer').assertFails
    }

    def 'test safe navigation'() {
        expect:
        !convertAndEvaluate('advanced/SafeNavigation').assertFails
    }

    def 'list ninja'() {
        expect:
        !convertAndEvaluate('advanced/ListNinja').assertFails
    }

    def 'maybe dsls'() {
        expect:
        !convertAndEvaluate('advanced/TryDsls').assertFails
    }

    def 'multiple conditions'() {
        expect:
        !convertAndEvaluate('advanced/MultipleConditions').assertFails
    }

    def 'method missing with this'() {
        expect:
        !convertAndEvaluate('advanced/MethodMissingTwo').assertFails
    }

    def 'curry, rcurry and ncurry'() {
        expect:
        !convertAndEvaluate('advanced/Curry').assertFails
    }

    def 'mixin annotation'() {
        expect:
        !convertAndEvaluate('advanced/MixinAst').assertFails
    }

    def 'variable number of arguments'() {
        expect:
        !convertAndEvaluate('advanced/VariableArguments').assertFails
    }

    def 'ranges of chars'() {
        expect:
        !convertAndEvaluate('advanced/RangeChars').assertFails
    }

    def 'return values'() {
        expect:
        !convertAndEvaluate('advanced/ReturnValues').assertFails
    }

    def 'test method pointer advanced'() {
        expect:
        !convertAndEvaluate('advanced/MethodPointerAdvanced').assertFails
    }

    def 'test callback this'() {
        expect:
        !convertAndEvaluate('advanced/CallbackThis').assertFails
    }

    def 'test property missing'() {
        expect:
        !convertAndEvaluate('advanced/PropertyMissing').assertFails
    }
}
