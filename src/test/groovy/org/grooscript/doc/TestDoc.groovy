package org.grooscript.doc

import org.grooscript.test.ConversionMixin
import spock.lang.Specification
import spock.lang.Unroll

/**
 * JFL 13/12/14
 */
@Mixin([ConversionMixin])
class TestDoc extends Specification {

    @Unroll
    def 'doc tests'() {
        expect:
        convertAndEvaluate filePath

        where:
        filePath << ['doc/Inheritance', 'doc/Object', 'doc/LittleFeatures', 'doc/Operators',
                     'doc/Truth', 'doc/Beans', 'doc/Metaprogramming']
    }
}
