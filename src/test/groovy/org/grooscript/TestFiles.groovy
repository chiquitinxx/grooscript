package org.grooscript

import org.grooscript.test.ConversionMixin
import org.grooscript.test.JavascriptEngine
import org.grooscript.util.GrooScriptException
import org.grooscript.util.Util
import spock.lang.IgnoreIf
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * User: jorgefrancoleza
 * Date: 22/11/13
 */
@Mixin([ConversionMixin])
class TestFiles extends Specification {

    Map options

    def setup() {
        options = [classPath: 'src/test/src']
    }

    def 'initial inheritance on distinct files'() {
        when:
        def result = convertFile('files/Car', options)

        then:
        !result.contains('function Vehicle()')
    }

    def 'check inheritance use in other files throw error cause father not converted'() {
        when:
        convertAndEvaluate('files/Vehicles', false, options)

        then:
        thrown(GrooScriptException)
    }

    def 'convert all inheritance files'() {

        when:
        def jsCar = convertFile('files/Car', options)

        then:
        jsCar.contains('function Car()')
        !jsCar.contains('function Vehicle()')

        when:
        def jsVehicle = convertFile('files/Vehicle', options)
        def jsVehicles = convertFile('files/Vehicles', options)
        def result = JavascriptEngine.jsEval(jsVehicle + jsCar + jsVehicles)

        then:
        !result.assertFails
    }

    @IgnoreIf({ !Util.groovyVersionAtLeast('2.3') })
    def 'convert a class with a trait in other file'() {
        when:
        def converted = convertFile('files/UsingTrait', options)

        then:
        converted.contains('gSobject.hello = function() { return MyTrait.hello(gSobject); }')
        converted.contains('return "Bye!";')
    }

    @Unroll
    def 'convert a file with AST'() {
        when:
        def converted = convertFile('files/Train', options)

        then:
        converted.contains('inMovement')
    }
}
