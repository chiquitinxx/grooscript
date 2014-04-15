package org.grooscript

import org.grooscript.test.ConversionMixin
import org.grooscript.test.JsTestResult
import spock.lang.Specification

/**
 * Class tests
 * JFL 27/08/12
 */
@Mixin([ConversionMixin])
class TestAdvancedClass extends Specification {

    def 'test class names' () {
        expect:
        !convertAndEvaluateWithJsEngine('classes/Names').assertFails
    }

    def 'test instanceof basic'() {
        expect:
        !convertAndEvaluateWithJsEngine('classes/InstanceOf').assertFails
    }

    def 'add methods and properties to classes'() {
        expect:
        !convertAndEvaluateWithJsEngine('classes/AddingStuff').assertFails
    }

    def 'who knows categories'() {
        expect:
        !convertAndEvaluateWithJsEngine('classes/Categories').assertFails
    }

    def 'mixins to the hell'() {
        expect:
        !convertAndEvaluateWithJsEngine('classes/Mixins').assertFails
    }

    def 'string buffer'() {
        when:
        JsTestResult result = convertAndEvaluateWithJsEngine('classes/StringBufferClass')

        then:
        result.console == 'hello!'
        !result.assertFails
    }

    def 'abstract class basic usage'() {
        expect:
        !convertAndEvaluateWithJsEngine('classes/Abstract').assertFails
    }

    def 'using @Category'() {
        expect:
        !convertAndEvaluateWithJsEngine('classes/AddCategories').assertFails
    }

    def 'using supported types'() {
        expect:
        !convertAndEvaluateWithJsEngine('classes/SupportedTypes').assertFails
    }

    def 'using as keyword'() {
        expect:
        !convertAndEvaluateWithJsEngine('classes/AsKeyword',).assertFails
    }

    def 'using primitive arrays'() {
        expect:
        !convertAndEvaluateWithJsEngine('classes/PrimitiveArrays').assertFails
    }

    def 'starting @Delegate'() {
        expect:
        !convertAndEvaluateWithJsEngine('classes/StartingDelegate').assertFails
    }

    def 'more categories'() {
        expect:
        !convertAndEvaluateWithJsEngine('classes/MoreCategories').assertFails
    }

    def 'date functions'() {
        expect:
        !convertAndEvaluateWithJsEngine('classes/DateClass').assertFails
    }

    def 'default method call'() {
        expect:
        !convertAndEvaluateWithJsEngine('classes/DefaultMethodCall').assertFails
    }
}
