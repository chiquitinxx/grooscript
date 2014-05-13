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
        convertAndEvaluate('advanced/StringSecrets')
    }

    def 'object comparation'() {
        expect:
        convertAndEvaluate('advanced/Comparable')
    }

    def 'more list and maps features'() {
        expect:
        convertAndEvaluate('advanced/ListMapsAdvanced')
    }

    def 'Get properties and methods of classes'() {
        expect:
        convertAndEvaluate('advanced/PropertiesAndMethods')
    }

    def 'Get tuple from object'() {
        expect:
        convertAndEvaluate('advanced/GetTupleFromObject')
    }

    def 'test method pointer'() {
        expect:
        convertAndEvaluate('advanced/MethodPointer')
    }

    def 'test safe navigation'() {
        expect:
        convertAndEvaluate('advanced/SafeNavigation')
    }

    def 'list ninja'() {
        expect:
        convertAndEvaluate('advanced/ListNinja')
    }

    def 'maybe dsls'() {
        expect:
        convertAndEvaluate('advanced/TryDsls')
    }

    def 'multiple conditions'() {
        expect:
        convertAndEvaluate('advanced/MultipleConditions')
    }

    def 'method missing with this'() {
        expect:
        convertAndEvaluate('advanced/MethodMissingTwo')
    }

    def 'curry, rcurry and ncurry'() {
        expect:
        convertAndEvaluate('advanced/Curry')
    }

    def 'mixin annotation'() {
        expect:
        convertAndEvaluate('advanced/MixinAst')
    }

    def 'variable number of arguments'() {
        expect:
        convertAndEvaluate('advanced/VariableArguments')
    }

    def 'ranges of chars'() {
        expect:
        convertAndEvaluate('advanced/RangeChars')
    }

    def 'return values'() {
        expect:
        convertAndEvaluate('advanced/ReturnValues')
    }

    def 'test method pointer advanced'() {
        expect:
        convertAndEvaluate('advanced/MethodPointerAdvanced')
    }

    def 'test callback this'() {
        expect:
        convertAndEvaluate('advanced/CallbackThis')
    }

    def 'test property missing'() {
        expect:
        convertAndEvaluate('advanced/PropertyMissing')
    }

    def 'test get boolean properties with is functions'() {
        expect:
        convertAndEvaluate('advanced/BooleanProperties')
    }
}
