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
class TestConversionFails extends Specification {

    def 'test fail assertion' () {
        when:
        def result = convertAndEvaluate('fail/assertFail')

        then:
        result.assertFails
        result.gSconsole == 'WOTT - false'
    }

    def 'test fail compile' () {
        when:
        def result = converter.toJs("a='Hello")

        then:
        Exception e = thrown()
        e.message.startsWith 'Compiler ERROR on Script'
    }

    def 'access metaClass of groovy and java types not allowed'() {
        when:
        converter.toJs("String.metaClass.grita = {\n" +
                "    return delegate+'!'\n" +
                "}")

        then:
        Exception e = thrown()
        e.message.startsWith 'Compiler END ERROR on Script -Not allowed access metaClass'
    }
}
