package org.grooscript.doc

import org.grooscript.test.ConversionMixin
import spock.lang.Specification

/**
 * JFL 13/12/14
 */
@Mixin([ConversionMixin])
class TestDoc extends Specification {

    def 'test inheritance' () {
        expect:
        convertAndEvaluate 'doc/Inheritance'
    }

    def 'test object' () {
        expect:
        convertAndEvaluate 'doc/Object'
    }
}
