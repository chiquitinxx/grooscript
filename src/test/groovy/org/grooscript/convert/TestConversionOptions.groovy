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

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

import static org.grooscript.util.Util.LINE_SEPARATOR as LS
import static org.grooscript.util.Util.SEP

class TestConversionOptions extends GroovyTestCase {

    private static final FILE_BASIC_NAME = 'BasicClass'
    private static final FILE_BASIC_GROOVY_SOURCE = "src/test/resources/classes/${FILE_BASIC_NAME}.groovy"
    private static final FOLDER_NEED_DEPENDENCY = 'need'
    private static final CLASS_NEED_DEPENDENCY = 'Need'
    private static final FILE_BASIC_JS = "${FOLDER_NEED_DEPENDENCY}${SEP}${FILE_BASIC_NAME}.js"
    private static final SOURCE_DIR = 'source'
    private static final DESTINATION_DIR = 'destination'
    private static final DESTINATION_FILE = 'destination.js'

    void setUp() {
        new File(SOURCE_DIR).mkdir()
        new File(DESTINATION_DIR).mkdir()
    }

    void tearDown() {
        new File(FOLDER_NEED_DEPENDENCY).deleteDir()
        new File(SOURCE_DIR).deleteDir()
        new File(DESTINATION_FILE).delete()
        new File(DESTINATION_DIR).deleteDir()
    }

    void testNumberConversionOptions() {
        assert ConversionOptions.values().size() == 10
    }

    void testConvertAGroovyFile() {
        def name = 'name'
        createFolderWithFiles(SOURCE_DIR, 1, name)

        GrooScript.convert("${SOURCE_DIR + SEP + name}0.groovy", DESTINATION_DIR)

        destinationDirContainsFiles(DESTINATION_DIR, 1)
    }

    void testConvertAListOfGroovyFiles() {
        def name = 'name'
        createFolderWithFiles(SOURCE_DIR, 2, name)

        GrooScript.convert(["${SOURCE_DIR + SEP + name}0.groovy",
                "${SOURCE_DIR + SEP + name}1.groovy"], DESTINATION_DIR)

        destinationDirContainsFiles(DESTINATION_DIR, 2)
    }

    void testCheckDependencyResolution() {

        setupNeedDirectory()
        def options = [classpath: FOLDER_NEED_DEPENDENCY]

        String result = GrooScript.convert("class A {};def need = new ${CLASS_NEED_DEPENDENCY}()", options)

        assert result.startsWith('function A()')
        assert result.endsWith("var need = ${CLASS_NEED_DEPENDENCY}();${Util.LINE_SEPARATOR}")

        assert !result.contains("function ${CLASS_NEED_DEPENDENCY}()")
    }

    void testIncludeDependenciesInConversion() {

        setupNeedDirectory()
        def options = [classpath: FOLDER_NEED_DEPENDENCY, includeDependencies: true]

        String result = GrooScript.convert("class A {};def need = new ${CLASS_NEED_DEPENDENCY}()", options)

        assert result.count('function A()') == 1
        assert result.count("function ${CLASS_NEED_DEPENDENCY}()") == 1
    }

    void testCanSetClasspathAsList() {

        setupNeedDirectory()
        def options = [classpath: [FOLDER_NEED_DEPENDENCY]]

        String result = GrooScript.convert("class B { Need c = new Need() }", options)

        assert result
        assert !result.contains('function Need()')
        assert result.contains('c = Need();')
    }

    void testConvertAFile() {

        setupNeedDirectory()

        GrooScript.convert(FILE_BASIC_GROOVY_SOURCE, FOLDER_NEED_DEPENDENCY)
        def file = new File(FILE_BASIC_JS)

        assert file.text.startsWith("function ${FILE_BASIC_NAME}()")
    }

    void testCustomizationOptionWithATypeCheckCustomization() {

        [groovy.transform.TypeChecked, groovy.transform.CompileStatic].each { typeAst ->
            def customization = {
                ast(typeAst)
            }
            def options = [customization: customization]
            def message = shouldFail (GrooScriptException) {
                GrooScript.convert('class A {  def say() { println aaaa }}', options)
            }
            assert message.contains('The variable [aaaa] is undeclared')

        }
    }

    void testJoinJsFilesInOneFile() {

        setupFilesWithNumbers(SOURCE_DIR, 5)

        GrooScript.joinFiles(SOURCE_DIR, DESTINATION_FILE)
        File file = new File(DESTINATION_FILE)

        ('0'..'4').every {
            assert file.text.indexOf(it) >= 0
        }
    }

    void testDefineMainContextScopeVariables() {
        def code = 'def addToB = { a ->  console.log("Hello!"); a + b }'
        def options = [mainContextScope: ['b', 'console']]

        def result = GrooScript.convert(code, options)

        assert result == "var addToB = function(a) {$LS" +
                  "  gs.mc(console,\"log\",[\"Hello!\"]);$LS" +
                  "  return gs.plus(a, b);$LS" +
                  " };$LS"
    }

    void testAddTextBeforeInGeneratedResult() {
        def code = 'def a = 0'
        def options = [initialText: 'Text']

        def result = GrooScript.convert(code, options)

        assert result == "Text${LS}var a = 0;${LS}"
    }

    void testAddTextAfterInGeneratedResult() {
        def code = 'def a = 0'
        def options = [finalText: 'Text']

        def result = GrooScript.convert(code, options)

        assert result == "var a = 0;${LS}${LS}Text"
    }

    void testRecursiveConversion() {
        [0, 1, 3].each { sourceFiles ->
            createFolderWithSubfolderAndFilesInEachDir(SOURCE_DIR, sourceFiles)

            GrooScript.convert(SOURCE_DIR, DESTINATION_DIR, [recursive: true])

            destinationDirContainsFiles(DESTINATION_DIR, sourceFiles * 2)
        }
    }

    void testAddGrooscriptJsArchiveAtTheBeginningOfTheConversion() {
        ['grooscript', 'grooscript.min'].each { fileName ->
            def result = GrooScript.convert('println "Hello!"', [addGsLib: fileName])

            assert result.startsWith(new File("src/main/resources/${fileName}.js").text)
        }
    }

    void testAddTwoGrooscriptJsArchivesAtTheBeginning() {
        def options = [addGsLib: 'grooscript.min, testWithNode']
        def result = GrooScript.convert('println "Hello!"', options)

        assert result.startsWith(new File('src/main/resources/grooscript.min.js').text)
        assert result.contains(new File('src/main/resources/testWithNode.js').text)
    }

    void testConversionWithConsoleInfo() {
        GrooScript.convert('class A {}', [consoleInfo: true])
    }

    void testConvertUsingNashornEngineToPrintInConsole() {

        ScriptEngineManager factory = new ScriptEngineManager()
        ScriptEngine engine = factory.getEngineByName('JavaScript')

        try {
            engine.eval(
                    GrooScript.convert('println "Hello World!"',
                            [(ConversionOptions.ADD_GS_LIB.text)     : 'grooscript',
                             (ConversionOptions.NASHORN_CONSOLE.text): true]))
            assert true
        } catch (Throwable t) {
            fail('No!')
        }
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
