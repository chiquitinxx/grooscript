/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

@Mixin([ConversionMixin])
class TestFiles extends Specification {

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
        def result = GrooScript.convertRequireJs("${sourceFolder}files${SEP}Car.groovy", destinationFolder, options)

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
        GrooScript.convertRequireJs("${sourceFolder}files${SEP}Vehicles.groovy", destinationFolder, options)

        then:
        !new File("${destinationFolder}${SEP}files${SEP}Vehicles.js").text.
                contains('return script')

        cleanup:
        new File(destinationFolder).deleteDir()
    }

    void 'convert requirejs using trait'() {
        when:
        GrooScript.convertRequireJs("${sourceFolder}files${SEP}UsingTrait.groovy", destinationFolder, options)

        then:
        folderContainsFiles("${destinationFolder}${SEP}files", ['MyTrait.js', 'UsingTrait.js'])

        cleanup:
        new File(destinationFolder).deleteDir()
    }

    void 'convert requirejs with ast'() {
        when:
        GrooScript.convertRequireJs("${sourceFolder}files${SEP}Train.groovy", destinationFolder, options)

        then:
        folderContainsFiles("${destinationFolder}${SEP}files", ['Train.js'])
        new File("${destinationFolder}${SEP}files${SEP}Train.js").text.contains("gSobject.inMovement = false;")

        cleanup:
        new File(destinationFolder).deleteDir()
    }

    void 'convert requirejs with circular dependencies'() {
        when:
        GrooScript.convertRequireJs("${sourceFolder}files${SEP}Garage.groovy", destinationFolder, options)

        then:
        folderContainsFiles("${destinationFolder}${SEP}files", ['Car.js', 'Garage.js', 'Vehicle.js'])

        cleanup:
        new File(destinationFolder).deleteDir()
    }

    void 'convert requirejs with require js (ast) module'() {
        when:
        GrooScript.convertRequireJs("${sourceFolder}files${SEP}Require.groovy", destinationFolder, options)
        def resultFile = new File("${destinationFolder}${SEP}files${SEP}Require.js")

        then:
        folderContainsFiles("${destinationFolder}${SEP}files", ['Require.js'])
        resultFile.text.startsWith("define(['lib/data'], function (data) {")

        cleanup:
        new File(destinationFolder).deleteDir()
    }

    void 'convert requirejs a non exists file, then an exception thrown'() {
        when:
        GrooScript.convertRequireJs("${sourceFolder}files${SEP}none.groovy", destinationFolder, options)

        then:
        thrown(GrooScriptException)

        cleanup:
        new File(destinationFolder).deleteDir()
    }

    void 'convert file with included dependencies, not repeating converted code'() {
        given:
        def conversionOptions = options
        options[ConversionOptions.INCLUDE_DEPENDENCIES.text] = true

        when:
        GrooScript.convert("${sourceFolder}files${SEP}Car.groovy", destinationFolder, conversionOptions)
        def destinationFile = new File("${destinationFolder}${SEP}Car.js")

        then:
        destinationFile.text.count('function Car() {') == 1
        destinationFile.text.count('function Garage() {') == 1
        destinationFile.text.count('function Vehicle() {') == 1

        cleanup:
        new File(destinationFolder).deleteDir()
    }

    void 'convert an interface does nothing'() {
        when:
        GrooScript.convert("${sourceFolder}files${SEP}interfaces${SEP}AnInterface.groovy", destinationFolder, options)
        def destinationFile = new File("${destinationFolder}${SEP}AnInterface.js")

        then:
        !destinationFile.exists()
    }

    private static final String FILES_CLASSPATH = "src${SEP}test${SEP}src"

    private Map options
    private destinationFolder = 'reqjs'
    private sourceFolder = "src${SEP}test${SEP}src${SEP}"

    def setup() {
        options = [classpath: FILES_CLASSPATH]
    }

    private boolean folderContainsFiles(String pathFolder, List<String> files) {
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
