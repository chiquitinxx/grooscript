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
import org.grooscript.util.GrooScriptException
import org.grooscript.util.Util
import spock.lang.IgnoreIf
import spock.lang.Specification
import spock.lang.Unroll

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

import static org.grooscript.util.Util.LINE_SEPARATOR as LS
import static org.grooscript.util.Util.SEP

class TestConversionOptions extends Specification {

    private static final FILE_BASIC_NAME = 'BasicClass'
    private static final FILE_BASIC_GROOVY_SOURCE = "src/test/resources/classes/${FILE_BASIC_NAME}.groovy"
    private static final FOLDER_NEED_DEPENDENCY = 'need'
    private static final CLASS_NEED_DEPENDENCY = 'Need'
    private static final FILE_BASIC_JS = "${FOLDER_NEED_DEPENDENCY}${SEP}${FILE_BASIC_NAME}.js"
    private static final SOURCE_DIR = 'source'
    private static final DESTINATION_DIR = 'destination'
    private static final DESTINATION_FILE = 'destination.js'

    def setup() {
        new File(SOURCE_DIR).mkdir()
        new File(DESTINATION_DIR).mkdir()
    }

    def cleanup() {
        new File(FOLDER_NEED_DEPENDENCY).deleteDir()
        new File(SOURCE_DIR).deleteDir()
        new File(DESTINATION_FILE).delete()
        new File(DESTINATION_DIR).deleteDir()
    }

    def 'number conversion options'() {
        expect:
        ConversionOptions.values().size() == 11
    }

    def 'convert a groovy file'() {
        given:
        def name = 'name'
        createFolderWithFiles(SOURCE_DIR, 1, name)

        when:
        GrooScript.convert("${SOURCE_DIR + SEP + name}0.groovy", DESTINATION_DIR)

        then:
        destinationDirContainsFiles(DESTINATION_DIR, 1)
    }

    def 'convert a list of groovy files'() {
        given:
        def name = 'name'
        createFolderWithFiles(SOURCE_DIR, 2, name)

        when:
        GrooScript.convert(["${SOURCE_DIR + SEP + name}0.groovy",
                "${SOURCE_DIR + SEP + name}1.groovy"], DESTINATION_DIR)

        then:
        destinationDirContainsFiles(DESTINATION_DIR, 2)
    }

    def 'check dependency resolution'() {

        given:
        setupNeedDirectory()
        def options = [classpath: FOLDER_NEED_DEPENDENCY]

        when: 'convert a class with need dependency'
        String result = GrooScript.convert("class A {};def need = new ${CLASS_NEED_DEPENDENCY}()", options)

        then:
        result.startsWith('function A()')
        result.endsWith("var need = ${CLASS_NEED_DEPENDENCY}();${Util.LINE_SEPARATOR}")

        and: 'class need not converted'
        !result.contains("function ${CLASS_NEED_DEPENDENCY}()")
    }

    def 'include dependencies in conversion'() {

        given:
        setupNeedDirectory()
        def options = [classpath: FOLDER_NEED_DEPENDENCY, includeDependencies: true]

        when:
        String result = GrooScript.convert("class A {};def need = new ${CLASS_NEED_DEPENDENCY}()", options)

        then:
        result.count('function A()') == 1
        result.count("function ${CLASS_NEED_DEPENDENCY}()") == 1
    }

    def 'can set classpath as List'() {

        given:
        setupNeedDirectory()
        def options = [classpath: [FOLDER_NEED_DEPENDENCY]]

        when:
        String result = GrooScript.convert("class B { Need c = new Need() }", options)

        then: 'conversion done'
        result
        !result.contains('function Need()')
        result.contains('c = Need();')
    }

    def 'convert a file'() {

        given:
        setupNeedDirectory()

        when: 'no dependencies to convert in a file in a package'
        GrooScript.convert(FILE_BASIC_GROOVY_SOURCE, FOLDER_NEED_DEPENDENCY)
        def file = new File(FILE_BASIC_JS)

        then: 'Conversion returns data converted'
        file.text.startsWith("function ${FILE_BASIC_NAME}()")
    }

    @IgnoreIf({ !Util.groovyVersionAtLeast('2.1') })
    @Unroll
    def 'customization option with a type check customization'() {

        given:
        def customization = {
            ast(astChecking)
        }
        def options = [customization: customization]

        when:
        GrooScript.convert('class A {  def say() { println aaaa }}', options)

        then:
        def exception = thrown(GrooScriptException)
        exception.message.contains 'The variable [aaaa] is undeclared'

        where:
        astChecking << [groovy.transform.TypeChecked, groovy.transform.CompileStatic]
    }

    def 'test join js files in one file'() {

        given:
        setupFilesWithNumbers(SOURCE_DIR, 5)

        when:
        GrooScript.joinFiles(SOURCE_DIR, DESTINATION_FILE)
        File file = new File(DESTINATION_FILE)

        then:
        ('0'..'4').every {
            file.text.indexOf(it) >= 0
        }
    }

    def 'define main context scope variables'() {
        given:
        def code = 'def addToB = { a ->  console.log("Hello!"); a + b }'
        def options = [mainContextScope: ['b', 'console']]

        when:
        def result = GrooScript.convert(code, options)

        then:
        result == "var addToB = function(a) {$LS" +
                  "  gs.mc(console,\"log\",[\"Hello!\"]);$LS" +
                  "  return gs.plus(a, b);$LS" +
                  " };$LS"
    }

    @Unroll
    def 'add text before and after in generated result'() {
        given:
        def code = 'def a = 0'
        def options = [:]

        when:
        options[option] = 'Text'
        def result = GrooScript.convert(code, options)

        then:
        result == expectedResult

        where:
        option        | expectedResult
        'initialText' | "Text${LS}var a = 0;${LS}"
        'finalText'   | "var a = 0;${LS}${LS}Text"
    }

    @Unroll
    def 'test recursive conversion'() {
        given:
        createFolderWithSubfolderAndFilesInEachDir(SOURCE_DIR, sourceFiles)
        def options = [recursive: true]

        when:
        GrooScript.convert(SOURCE_DIR, DESTINATION_DIR, options)

        then:
        destinationDirContainsFiles(DESTINATION_DIR, destinationFiles)

        where:
        sourceFiles | destinationFiles
        0           | 0
        1           | 2
        3           | 6
    }

    @Unroll
    def 'test add grooscript js archive at the beginning of the conversion'() {
        when:
        def options = [addGsLib: fileName]
        def result = GrooScript.convert('println "Hello!"', options)

        then:
        result.startsWith(new File("src/main/resources/META-INF/resources/${fileName}.js").text)

        where:
        fileName << ['grooscript', 'grooscript.min']
    }

    def 'test add two grooscript js archives at the beginning of the conversion'() {
        when:
        def options = [addGsLib: 'grooscript.min, testWithNode']
        def result = GrooScript.convert('println "Hello!"', options)

        then:
        result.startsWith(new File('src/main/resources/META-INF/resources/grooscript.min.js').text)
        result.contains(new File('src/main/resources/META-INF/resources/testWithNode.js').text)
    }

    def 'test convert a class as require.js module'() {
        when:
        def asRequireJsModuleResult = GrooScript.convert('class A {}', [requireJsModule: true])
        def normalConversion = GrooScript.convert('class A {}')

        then:
        asRequireJsModuleResult != normalConversion
    }

    def 'conversion with consoleInfo'() {
        expect:
        GrooScript.convert('class A {}', [consoleInfo: true])
    }

    def 'convert using nashorn engine to print in console'() {
        given:
        ScriptEngineManager factory = new ScriptEngineManager()
        ScriptEngine engine = factory.getEngineByName('JavaScript')

        when:
        engine.eval(
                GrooScript.convert('println "Hello World!"',
                        [(ConversionOptions.ADD_GS_LIB.text): 'grooscript',
                         (ConversionOptions.NASHORN_CONSOLE.text): true]))

        then:
        notThrown(Throwable)
    }

    private setupNeedDirectory() {
        new File(FOLDER_NEED_DEPENDENCY).mkdir()
        new File(FOLDER_NEED_DEPENDENCY + SEP + CLASS_NEED_DEPENDENCY + '.groovy') <<
                "class ${CLASS_NEED_DEPENDENCY} {}"
    }

    private setupFilesWithNumbers(name, number) {
        new File(name).mkdir()
        number.times {
            new File(name + SEP + it + '.js') << it as String
        }
    }

    private createFolderWithFiles(sourceDir, numberOfFilesInside, name = 'name') {
        new File(sourceDir).mkdir()
        numberOfFilesInside.times {
            new File(sourceDir + SEP + name + it + '.groovy') << it as String
        }
    }

    private createFolderWithSubfolderAndFilesInEachDir(sourceDir, numberOfFilesInEachDir) {
        createFolderWithFiles(sourceDir, numberOfFilesInEachDir, 'base')
        createFolderWithFiles(sourceDir + SEP + 'inside', numberOfFilesInEachDir, 'inside')
    }

    private void destinationDirContainsFiles(destinationDir, numberTotalOfFiles) {
        assert numberTotalOfFiles == new File(destinationDir).listFiles().size()
    }
}
