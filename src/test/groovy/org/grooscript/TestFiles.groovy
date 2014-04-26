package org.grooscript

import org.grooscript.test.ConversionMixin
import org.grooscript.test.JavascriptEngine
import org.grooscript.util.GrooScriptException
import spock.lang.Specification

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
        result.contains('function Vehicle()')
    }

    def 'check inheritance use in other files with convertDependencies'() {
        when:
        def result = convertAndEvaluate('files/Vehicles', true, options)

        then:
        notThrown(GrooScriptException)
        result
    }

    def 'inheritance without convert dependencies'() {
        given:
        options << [convertDependencies: false]

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

    def 'convert a @GsNative method in a dependency file'() {
        when:
        def converted = convertFile('files/UseGsNative', options)
        println converted

        then:
        converted.contains('alert(\'Hello!\');')
    }
}
