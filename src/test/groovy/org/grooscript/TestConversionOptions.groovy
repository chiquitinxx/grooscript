package org.grooscript

import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 14/02/13
 */
class TestConversionOptions extends Specification {

    //Reset options of GrooScript
    def setup() {
        GrooScript.setOwnClassPath(null)
        GrooScript.setConversionProperty('convertDependencies',true)
    }

    def 'check dependency resolution'() {

        when:
        GrooScript.setOwnClassPath('need')
        def String result = GrooScript.convert("class A {};def need = new Need()")

        then:
        result
        result.startsWith('function A()')
        result.endsWith('var need = Need();\n')
    }

    def 'check dependency resolution alone'() {

        when:
        //now not fails in gradle
        GrooScript.setOwnClassPath('need')
        def String result = GrooScript.convert("class B { def Need c}")
        //println result

        then:
        result
        result.startsWith('function B()')
        result.contains('gSobject.c = null;\n')
    }

    def 'testing dependency conversions'() {

        when: 'we dont want dependencies classes in the javascript result, in this case Need class'
        GrooScript.setOwnClassPath('need')
        GrooScript.setConversionProperty('convertDependencies',false)
        def String result = GrooScript.convert("class B { def Need c}")
        //println result

        then: 'Need class not converted'
        result
        !result.contains('function Need()')
    }

    def 'can set classpath as List'() {
        when: 'we set classpath as list'
        GrooScript.setOwnClassPath(['need'])
        def String result = GrooScript.convert("class B { def Need c}")

        then: 'not fails and Need converted'
        result
        result.contains('function Need()')
    }
}
