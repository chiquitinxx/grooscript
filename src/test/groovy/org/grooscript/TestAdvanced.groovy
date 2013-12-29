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
        !readAndConvert('advanced/Tree').assertFails
    }

    def 'expando world' () {
        expect:
        !readAndConvert('advanced/ExpandoWorld').assertFails
    }

    def 'mystic table' () {
        expect:
        !readAndConvert('advanced/MysticTable').assertFails
    }

    def 'summer function callings' () {
        expect:
        !readAndConvert('advanced/summer').assertFails
    }

    def 'regular expressions' () {
        expect:
        !readAndConvert('advanced/RegularExpressions').assertFails
    }

    def 'random world' () {
        expect:
        !readAndConvert('advanced/RandomWorld').assertFails
    }

    def 'test Robot'() {
        expect:
        !readAndConvert('advanced/SampleRobot').assertFails
    }

    def 'closuring and maps again' () {
        expect:
        !readAndConvert('advanced/ClosuringRevisited').assertFails
    }

    def 'sorting lists' () {
        expect:
        !readAndConvert('advanced/Sorting').assertFails
    }

    def 'features 0.1' () {
        expect:
        !readAndConvert('features/ZeroOne').assertFails
    }

    def 'mastering scope'() {
        expect:
        !readAndConvert('advanced/MasterScoping').assertFails
    }

    def 'test setters'() {
        expect:
        !readAndConvert('advanced/Setters').assertFails
    }

    def 'test getters'() {
        expect:
        !readAndConvert('advanced/Getters').assertFails
    }

    def 'test getter and setters'() {
        expect:
        !readAndConvert('advanced/GettersAndSetters').assertFails
    }

    def 'test missing method'() {
        expect:
        !readAndConvert('advanced/MethodMissing').assertFails
    }

    def 'web example'() {
        expect:
        !readAndConvert('advanced/WebExample').assertFails
    }

    def 'advanced web example'() {
        expect:
        !readAndConvert('advanced/AdvancedWebExample').assertFails
    }

    def 'more string features'() {
        expect:
        !readAndConvert('advanced/StringSecrets').assertFails
    }

    def 'object comparation'() {
        expect:
        !readAndConvert('advanced/Comparable').assertFails
    }

    def 'more list and maps features'() {
        expect:
        !readAndConvert('advanced/ListMapsAdvanced').assertFails
    }

    def 'Get properties and methods of classes'() {
        expect:
        !readAndConvert('advanced/PropertiesAndMethods').assertFails
    }

    def 'Get tuple from object'() {
        expect:
        !readAndConvert('advanced/GetTupleFromObject').assertFails
    }

    def 'test method pointer'() {
        expect:
        !readAndConvert('advanced/MethodPointer').assertFails
    }

    def 'test safe navigation'() {
        expect:
        !readAndConvert('advanced/SafeNavigation').assertFails
    }

    def 'list ninja'() {
        expect:
        !readAndConvert('advanced/ListNinja').assertFails
    }

    def 'maybe dsls'() {
        expect:
        !readAndConvert('advanced/TryDsls').assertFails
    }

    def 'multiple conditions'() {
        expect:
        !readAndConvert('advanced/MultipleConditions').assertFails
    }

    def 'method missing with this'() {
        expect:
        !readAndConvert('advanced/MethodMissingTwo').assertFails
    }

    def 'curry, rcurry and ncurry'() {
        expect:
        !readAndConvert('advanced/Curry').assertFails
    }

    def 'mixin annotation'() {
        expect:
        !readAndConvert('advanced/MixinAst').assertFails
    }

    def 'variable number of arguments'() {
        expect:
        !readAndConvert('advanced/VariableArguments').assertFails
    }

    def 'ranges of chars'() {
        expect:
        !readAndConvert('advanced/RangeChars').assertFails
    }

    def 'return values'() {
        expect:
        !readAndConvert('advanced/ReturnValues').assertFails
    }
}
