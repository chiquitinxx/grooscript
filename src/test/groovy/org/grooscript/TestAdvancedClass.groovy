package org.grooscript

import org.grooscript.test.ConversionMixin
import org.grooscript.test.JsTestResult
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Class tests
 * JFL 27/08/12
 */
@Mixin([ConversionMixin])
class TestAdvancedClass extends Specification {

    def 'test class names' () {
        expect:
        convertAndEvaluate('classes/Names')
    }

    def 'test instanceof basic'() {
        expect:
        convertAndEvaluate('classes/InstanceOf')
    }

    def 'add methods and properties to classes'() {
        expect:
        convertAndEvaluate('classes/AddingStuff')
    }

    def 'who knows categories'() {
        expect:
        convertAndEvaluate('classes/Categories')
    }

    def 'mixins to the hell'() {
        expect:
        convertAndEvaluate('classes/Mixins')
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
        convertAndEvaluate('classes/Abstract')
    }

    def 'using @Category'() {
        expect:
        convertAndEvaluate('classes/AddCategories')
    }

    def 'using supported types'() {
        expect:
        convertAndEvaluate('classes/SupportedTypes')
    }

    def 'using as keyword'() {
        expect:
        convertAndEvaluate('classes/AsKeyword')
    }

    def 'using primitive arrays'() {
        expect:
        convertAndEvaluate('classes/PrimitiveArrays')
    }

    def 'starting @Delegate'() {
        expect:
        convertAndEvaluate('classes/StartingDelegate')
    }

    def 'more categories'() {
        expect:
        convertAndEvaluate('classes/MoreCategories')
    }

    def 'date functions'() {
        expect:
        convertAndEvaluate('classes/DateClass')
    }

    def 'default method call'() {
        expect:
        convertAndEvaluate('classes/DefaultMethodCall')
    }

    def 'test inner classes'() {
        expect:
        convertAndEvaluate('classes/Inner')
    }

    def 'test static properties'() {
        expect:
        convertAndEvaluate('classes/StaticProperties')
    }
}
