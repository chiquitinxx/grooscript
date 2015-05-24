package org.grooscript.convert

import org.grooscript.GrooScript
import org.grooscript.convert.util.ConvertedFile
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

    private static final String FILES_CLASSPATH = "src${SEP}test${SEP}src"

    private Map options
    private destinationFolder = 'reqjs'
    private sourceFolder = "src${SEP}test${SEP}src${SEP}"

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
        def result = GrooScript.convertRequireJs("${sourceFolder}files${SEP}Car.groovy", destinationFolder)

        then:
        result == [new ConvertedFile("${sourceFolder}files${SEP}Vehicle.groovy", "files${SEP}Vehicle.js"),
                   new ConvertedFile("${sourceFolder}files${SEP}Garage.groovy", "files${SEP}Garage.js"),
                   new ConvertedFile("${sourceFolder}files${SEP}Car.groovy", "files${SEP}Car.js")]

        folderContainsFiles("${destinationFolder}${SEP}files", ['Car.js', 'Garage.js', 'Vehicle.js'])
        new File("${destinationFolder}${SEP}files${SEP}Car.js").text.
                startsWith('define([\'files/Vehicle\',\'files/Garage\'], function (Vehicle,Garage) {')
        new File("${destinationFolder}${SEP}files${SEP}Vehicle.js").text.
                startsWith('define(function () {')

        cleanup:
        new File(destinationFolder).deleteDir()
    }

    void 'convert requirejs Vehicles'() {
        when:
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, FILES_CLASSPATH)
        GrooScript.convertRequireJs("${sourceFolder}files${SEP}Vehicles.groovy", destinationFolder)

        then:
        !new File("${destinationFolder}${SEP}files${SEP}Vehicles.js").text.
                contains('return script')

        cleanup:
        new File(destinationFolder).deleteDir()
    }

    void 'convert requirejs using trait'() {
        when:
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, FILES_CLASSPATH)
        GrooScript.convertRequireJs("${sourceFolder}files${SEP}UsingTrait.groovy", destinationFolder)

        then:
        folderContainsFiles("${destinationFolder}${SEP}files", ['MyTrait.js', 'UsingTrait.js'])

        cleanup:
        new File(destinationFolder).deleteDir()
    }

    void 'convert requirejs with ast'() {
        when:
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, FILES_CLASSPATH)
        GrooScript.convertRequireJs("${sourceFolder}files${SEP}Train.groovy", destinationFolder)

        then:
        folderContainsFiles("${destinationFolder}${SEP}files", ['Train.js'])
        new File("${destinationFolder}${SEP}files${SEP}Train.js").text.contains("gSobject.inMovement = false;")

        cleanup:
        new File(destinationFolder).deleteDir()
    }

    void 'convert requirejs with circular dependencies'() {
        when:
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, FILES_CLASSPATH)
        GrooScript.convertRequireJs("${sourceFolder}files${SEP}Garage.groovy", destinationFolder)

        then:
        folderContainsFiles("${destinationFolder}${SEP}files", ['Car.js', 'Garage.js', 'Vehicle.js'])

        cleanup:
        new File(destinationFolder).deleteDir()
    }

    void 'convert requirejs with require js (ast) module'() {
        when:
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, FILES_CLASSPATH)
        GrooScript.convertRequireJs("${sourceFolder}files${SEP}Require.groovy", destinationFolder)
        def resultFile = new File("${destinationFolder}${SEP}files${SEP}Require.js")

        then:
        folderContainsFiles("${destinationFolder}${SEP}files", ['Require.js'])
        resultFile.text.startsWith("define(['lib/data'], function (data) {")

        cleanup:
        new File(destinationFolder).deleteDir()
    }

    void 'convert requirejs a non exists file, then an exception thrown'() {
        given:
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, FILES_CLASSPATH)

        when:
        GrooScript.convertRequireJs("${sourceFolder}files${SEP}none.groovy", destinationFolder)

        then:
        thrown(GrooScriptException)

        cleanup:
        new File(destinationFolder).deleteDir()
    }

    private boolean folderContainsFiles(String pathFolder, List files) {
        File folder = new File(pathFolder)
        boolean allFound = true
        int numberFiles = 0
        folder.eachFile {
            if (!(it.name in files)) {
                allFound = false
            }
            numberFiles++
        }
        allFound && numberFiles == files.size()
    }
}
