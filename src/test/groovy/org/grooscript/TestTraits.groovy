package org.grooscript

import org.grooscript.test.ConversionMixin
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 22/11/13
 */
@Mixin([ConversionMixin])
class TestTraits extends Specification {

    def 'initial traits support'() {
        expect:
        convertAndEvaluate('traits/Starting')
    }

    def 'abstract methods'() {
        expect:
        convertAndEvaluate('traits/AbstractMethods')
    }

    def 'private methods'() {
        expect:
        convertAndEvaluate('traits/PrivateMethods')
    }

    def 'inheritance and interfaces'() {
        expect:
        convertAndEvaluate('traits/Inheritance')
    }
}
