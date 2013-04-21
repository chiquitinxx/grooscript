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
class TestAdvancedClass extends Specification {

    def 'test class names' () {
        when:
        def result = readAndConvert('classes/Names',false,[addClassNames:true])

        then:
        !result.assertFails
    }

    def 'test instanceof basic'() {
        when:
        def result = readAndConvert('classes/InstanceOf',false,[addClassNames:true])

        then:
        !result.assertFails
    }

    def 'add methods and properties to classes'() {
        when:
        def result = readAndConvert('classes/AddingStuff',false,[addClassNames:true])

        then:
        !result.assertFails
    }

    def 'who knows categories'() {
        when:
        def result = readAndConvert('classes/Categories')

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'mixins to the hell'() {
        when:
        def result = readAndConvert('classes/Mixins',false,[addClassNames:true])

        then:
        //println 'Console->'+result.gSconsole
        !result.assertFails
    }

    def 'string buffer'() {
        when:
        def result = readAndConvert('classes/StringBufferClass')

        then:
        //println 'Console->'+result.gSconsole
        result.gSconsole == 'hello!'
        !result.assertFails
    }

}
