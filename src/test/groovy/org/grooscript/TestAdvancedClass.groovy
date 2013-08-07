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
        !readAndConvert('classes/Names',false,[addClassNames:true]).assertFails
    }

    def 'test instanceof basic'() {
        expect:
        !readAndConvert('classes/InstanceOf',false,[addClassNames:true]).assertFails
    }

    def 'add methods and properties to classes'() {
        expect:
        !readAndConvert('classes/AddingStuff',false,[addClassNames:true]).assertFails
    }

    def 'who knows categories'() {
        expect:
        !readAndConvert('classes/Categories').assertFails
    }

    def 'mixins to the hell'() {
        expect:
        !readAndConvert('classes/Mixins',false,[addClassNames:true]).assertFails
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
