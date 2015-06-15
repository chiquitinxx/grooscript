package org.grooscript.convert

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

    def 'with traits'() {
        expect:
        convertAndEvaluate('traits/WithTraits')
    }

    def 'as runtime'() {
        expect:
        convertAndEvaluate('traits/Runtime')
    }

    def 'use GsNative in traits'() {
        expect:
        convertAndEvaluate('traits/Native')
    }

    def 'initialization of trait fields'() {
        expect:
        convertAndEvaluate('traits/Initialization')
    }

    def 'method calling'() {
        expect:
        convertAndEvaluate('traits/MethodCalling')
    }

    @IgnoreIf({ !Util.groovyVersionAtLeast('2.4') })
    def 'static properties'() {
        expect:
        convertAndEvaluate('traits/Static')
    }

    def 'confusing method call'() {
        expect:
        convertAndEvaluate('traits/ConfusingMethod')
    }

    @IgnoreIf({ !Util.groovyVersionAtLeast('2.4') })
    def 'get object properties'() {
        expect:
        convertAndEvaluate('traits/ObjectProperties')
    }

    def 'using this in trait functions'() {
        expect:
        convertAndEvaluate('traits/UsingThis')
    }
}
