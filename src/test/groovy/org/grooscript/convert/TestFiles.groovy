package org.grooscript.convert

import org.grooscript.GrooScript
import org.grooscript.test.ConversionMixin
import org.grooscript.test.JavascriptEngine
import org.grooscript.util.GrooScriptException
import org.grooscript.util.Util
import spock.lang.IgnoreIf
import spock.lang.Specification
import spock.lang.Unroll

import static org.grooscript.util.Util.SEP
/**
 * User: jorgefrancoleza
 * Date: 22/11/13
 */
@Mixin([ConversionMixin])
class TestFiles extends Specification {

    private static final FILES_CLASSPATH = 'src/test/src'

    private Map options
    private destinationFolder = 'reqjs'

    def setup() {
        options = [classPath: FILES_CLASSPATH]
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
        converted.contains('gSobject.setName("UsingTrait");')
        converted.contains('MyTrait.$init$(gSobject);')
        converted.contains('gSobject.hello = function() { return MyTrait.hello(gSobject); }')
        converted.contains('return "Bye!";')

        when:
        converted = convertFile('files/MyTrait', options)

        then:
        converted.contains('function MyTrait$static$init$($static$self)')
        converted.contains('MyTrait.$init$ = function($self)')
    }

    @Unroll
    def 'convert a file with AST'() {
        when:
        def converted = convertFile('files/Train', options)

        then:
        converted.contains('inMovement')
    }


    void 'convert requirejs Car'() {
        when:
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, FILES_CLASSPATH)
        GrooScript.convertRequireJs("src${SEP}test${SEP}src${SEP}files${SEP}Car.groovy", destinationFolder)

        then:
        new File("${destinationFolder}${SEP}files").listFiles().collect { it.name } == ['Car.js', 'Vehicle.js']
        new File("${destinationFolder}${SEP}files${SEP}Car.js").text.
                startsWith('define([\'files/Vehicle\'], function (Vehicle) {')
        new File("${destinationFolder}${SEP}files${SEP}Vehicle.js").text.
                startsWith('define(function () {')

        cleanup:
        new File(destinationFolder).deleteDir()
    }

    void 'convert requirejs Vehicles'() {
        when:
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, FILES_CLASSPATH)
        GrooScript.convertRequireJs("src${SEP}test${SEP}src${SEP}files${SEP}Vehicles.groovy", destinationFolder)

        then:
        !new File("${destinationFolder}${SEP}files${SEP}Vehicles.js").text.
                contains('return script')

        cleanup:
        new File(destinationFolder).deleteDir()
    }

    void 'convert requirejs using trait'() {
        when:
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, FILES_CLASSPATH)
        GrooScript.convertRequireJs("src${SEP}test${SEP}src${SEP}files${SEP}UsingTrait.groovy", destinationFolder)

        then:
        new File("${destinationFolder}${SEP}files").listFiles().collect { it.name } == ['MyTrait.js', 'UsingTrait.js']

        cleanup:
        new File(destinationFolder).deleteDir()
    }

    void 'convert requirejs with ast'() {
        when:
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, FILES_CLASSPATH)
        GrooScript.convertRequireJs("src${SEP}test${SEP}src${SEP}files${SEP}Train.groovy", destinationFolder)

        then:
        new File("${destinationFolder}${SEP}files").listFiles().collect { it.name } == ['Train.js']
        new File("${destinationFolder}${SEP}files${SEP}Train.js").text.contains("gSobject.inMovement = false;")

        cleanup:
        new File(destinationFolder).deleteDir()
    }
}
