package org.grooscript

import org.grooscript.test.ConversionMixin
import spock.lang.Specification

/**
 * Class tests
 * JFL 27/08/12
 */
@Mixin([ConversionMixin])
class TestAdvancedClass extends Specification {

    def 'test class names' () {
        expect:
        !convertAndEvaluate('classes/Names').assertFails
    }

    def 'test instanceof basic'() {
        expect:
        !convertAndEvaluate('classes/InstanceOf').assertFails
    }

    def 'add methods and properties to classes'() {
        expect:
        !convertAndEvaluate('classes/AddingStuff').assertFails
    }

    def 'who knows categories'() {
        expect:
        !convertAndEvaluate('classes/Categories').assertFails
    }

    def 'mixins to the hell'() {
        expect:
        !convertAndEvaluate('classes/Mixins').assertFails
    }

    def 'string buffer'() {
        when:
        def result = convertAndEvaluate('classes/StringBufferClass')

        then:
        result.gSconsole == 'hello!'
        !result.assertFails
    }

    def 'abstract class basic usage'() {
        expect:
        !convertAndEvaluate('classes/Abstract').assertFails
    }

    def 'using @Category'() {
        expect:
        !convertAndEvaluate('classes/AddCategories').assertFails
    }

    def 'using supported types'() {
        expect:
        !convertAndEvaluate('classes/SupportedTypes').assertFails
    }

    def 'using as keyword'() {
        expect:
        !convertAndEvaluate('classes/AsKeyword',).assertFails
    }

    def 'using primitive arrays'() {
        expect:
        !convertAndEvaluate('classes/PrimitiveArrays').assertFails
    }

    def 'starting @Delegate'() {
        expect:
        !convertAndEvaluate('classes/StartingDelegate').assertFails
    }

    def 'more categories'() {
        expect:
        !convertAndEvaluate('classes/MoreCategories').assertFails
    }

    def 'date functions'() {
        expect:
        !convertAndEvaluate('classes/DateClass').assertFails
    }

    def 'default method call'() {
        expect:
        !convertAndEvaluate('classes/DefaultMethodCall').assertFails
    }
}
