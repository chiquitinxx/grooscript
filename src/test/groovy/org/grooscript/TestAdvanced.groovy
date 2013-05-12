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
        when:
        def result = readAndConvert('advanced/Tree')

        then:
        !result.assertFails
    }

    def 'expando world' () {
        when:
        def result = readAndConvert('advanced/ExpandoWorld')

        then:
        !result.assertFails
    }

    def 'mystic table' () {
        when:
        def result = readAndConvert('advanced/MysticTable')

        then:
        !result.assertFails
    }

    def 'summer function callings' () {
        when:
        def result = readAndConvert('advanced/summer')

        then:
        !result.assertFails
    }

    def 'regular expressions' () {
        when:
		def result = readAndConvert('advanced/RegularExpressions')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'random world' () {
        when:
        def result = readAndConvert('advanced/RandomWorld')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'test Robot'() {
        when:
        def result = readAndConvert('advanced/SampleRobot')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

    /*
    def 'test Robot'() {
        when:
        def result = readAndConvert('robot/Robot',true)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

    def 'test robot'() {
        when:
        def result = readAndConvert('robot/RobotGame',true)

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }
    */

    def 'closuring and maps again' () {
        when:
        def result = readAndConvert('advanced/ClosuringRevisited')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'sorting lists' () {
        when:
        def result = readAndConvert('advanced/Sorting')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

    def 'features 0.1' () {
        when:
        def result = readAndConvert('features/ZeroOne')

        then:
        !result.assertFails

    }

    def 'mastering scope'() {
        when:
        def result = readAndConvert('advanced/MasterScoping')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'test setters'() {
        when:
        def result = readAndConvert('advanced/Setters')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

    def 'test getters'() {
        when:
        def result = readAndConvert('advanced/Getters')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

    def 'test getter and setters'() {
        when:
        def result = readAndConvert('advanced/GettersAndSetters')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'test missing method'() {
        when:
        def result = readAndConvert('advanced/MethodMissing')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'web example'() {
        when:
        def result = readAndConvert('advanced/WebExample')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'advanced web example'() {
        when:
        def result = readAndConvert('advanced/AdvancedWebExample')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'more string features'() {
        when:
        def result = readAndConvert('advanced/StringSecrets')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'object comparation'() {
        when:
        def result = readAndConvert('advanced/Comparable',false,[addClassNames:true])

        then:
        !result.assertFails
    }

    def 'more list and maps features'() {
        when:
        def result = readAndConvert('advanced/ListMapsAdvanced')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'Get properties and methods of classes'() {
        when:
        def result = readAndConvert('advanced/PropertiesAndMethods')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'Get tuple from object'() {
        when:
        def result = readAndConvert('advanced/GetTupleFromObject')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'test method pointer'() {
        when:
        def result = readAndConvert('advanced/MethodPointer')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

    def 'test safe navigation'() {
        when:
        def result = readAndConvert('advanced/SafeNavigation')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

    def 'list ninja'() {
        when:
        def result = readAndConvert('advanced/ListNinja')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

    def 'maybe dsls'() {
        when:
        def result = readAndConvert('advanced/TryDsls')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails

    }

    def 'multiple conditions'() {
        when:
        def result = readAndConvert('advanced/MultipleConditions')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'method missing with this'() {
        when:
        def result = readAndConvert('advanced/MethodMissingTwo')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

}
