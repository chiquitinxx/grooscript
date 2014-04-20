package org.grooscript

import org.grooscript.test.ConversionMixin
import org.grooscript.util.Util
import spock.lang.IgnoreIf
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 22/11/13
 */
@Mixin([ConversionMixin])
@IgnoreIf({ !Util.groovyVersionAtLeast('2.3') })
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

    def 'properties'() {
        expect:
        convertAndEvaluate('traits/Properties')
    }

    def 'private fields'() {
        expect:
        convertAndEvaluate('traits/PrivateFields')
    }

    def 'composition and order'() {
        expect:
        convertAndEvaluate('traits/Composition')
    }

    def 'extending'() {
        expect:
        convertAndEvaluate('traits/Extending')
    }

    def 'dynamic'() {
        expect:
        convertAndEvaluate('traits/Dynamic')
    }
}
