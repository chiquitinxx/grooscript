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
        !readAndConvert('classes/Names').assertFails
    }

    def 'test instanceof basic'() {
        expect:
        !readAndConvert('classes/InstanceOf').assertFails
    }

    def 'add methods and properties to classes'() {
        expect:
        !readAndConvert('classes/AddingStuff').assertFails
    }

    def 'who knows categories'() {
        expect:
        !readAndConvert('classes/Categories').assertFails
    }

    def 'mixins to the hell'() {
        expect:
        !readAndConvert('classes/Mixins').assertFails
    }

    def 'string buffer'() {
        when:
        def result = readAndConvert('classes/StringBufferClass')

        then:
        result.gSconsole == 'hello!'
        !result.assertFails
    }

    def 'abstract class basic usage'() {
        expect:
        !readAndConvert('classes/Abstract').assertFails
    }
}
