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
import org.grooscript.test.ConversionMixin
import org.grooscript.test.JavascriptEngine
import org.grooscript.util.GrooScriptException

import static org.grooscript.util.Util.SEP

class TestFiles extends GroovyTestCase implements ConversionMixin {

    void testInitialInheritanceOnDistinctFiles() {
        def result = convertFile('files/Car', options)
        assert !result.contains('function Vehicle()')
    }

    void testInheritanceUseInOtherFilesThrowErrorCauseFatherNotConverted() {
        shouldFail(GrooScriptException) {
            convertAndEvaluate('files/Vehicles', false, options)
        }
    }

    void testConvertAllInheritanceFiles() {

        def jsCar = convertFile('files/Car', options)
        assert jsCar.contains('function Car()')
        assert !jsCar.contains('function Vehicle()')

        def jsVehicle = convertFile('files/Vehicle', options)
        def jsVehicles = convertFile('files/Vehicles', options)
        def result = JavascriptEngine.jsEval(jsVehicle + jsCar + jsVehicles)

        assert !result.assertFails
    }

    void testConvertAClassWithATraitInOtherFile() {
        def converted = convertFile('files/UsingTrait', options)

        assert converted.contains('gSobject.setName("UsingTrait");')
        assert converted.contains('MyTrait.$init$(gSobject);')
        assert converted.contains('gSobject.hello = function() { return MyTrait.hello(gSobject); }')
        assert converted.contains('return "Bye!";')

        converted = convertFile('files/MyTrait', options)

        assert converted.contains('function MyTrait$static$init$($static$self)')
        assert converted.contains('MyTrait.$init$ = function($self)')
    }

    void testConvertAFileWithAST() {
        def converted = convertFile('files/Train', options)
        assert converted.contains('inMovement')
    }

    void testConvertFileWithIncludedDependenciesNotRepeatingConvertedCode() {
        def conversionOptions = options
        options[ConversionOptions.INCLUDE_DEPENDENCIES.text] = true

        GrooScript.convert("${sourceFolder}files${SEP}Car.groovy", destinationFolder, conversionOptions)
        def destinationFile = new File("${destinationFolder}${SEP}Car.js")

        assert destinationFile.text.count('function Car() {') == 1
        assert destinationFile.text.count('function Garage() {') == 1
        assert destinationFile.text.count('function Vehicle() {') == 1

        new File(destinationFolder).deleteDir()
    }

    void testConvertAnInterfaceDoesNothing() {
        GrooScript.convert("${sourceFolder}files${SEP}interfaces${SEP}AnInterface.groovy", destinationFolder, options)
        def destinationFile = new File("${destinationFolder}${SEP}AnInterface.js")

        assert !destinationFile.exists()
    }

    private static final String FILES_CLASSPATH = "src${SEP}test${SEP}src"

    private Map options
    private destinationFolder = 'reqjs'
    private sourceFolder = "src${SEP}test${SEP}src${SEP}"

    void setUp() {
        options = [classpath: FILES_CLASSPATH]
    }
}
