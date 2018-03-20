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
package org.grooscript

import org.grooscript.util.GsConsole
import static org.grooscript.util.Util.LINE_SEPARATOR as LS

class GrooScriptSpec extends GroovyTestCase {

    void testDefaultConversionOptions() {
        assert GrooScript.defaultConversionOptions == [
                classpath: null,
                customization: null,
                mainContextScope: null,
                initialText: null,
                finalText: null,
                addGsLib: null,
                recursive: false,
                consoleInfo: false,
                includeDependencies: false,
                nashornConsole: false
        ]
    }

    void testConvertBigFile() {
        GrooScript.convert(SOURCES_FOLDER, BIG_JS_FILE, [classpath: SOURCES_CLASSPATH])
        new File(BIG_JS_FILE).exists()

        new File(BIG_JS_FILE).delete()
        new File(DEST_FOLDER).deleteDir()
    }

    void testConvertBigFileInOtherFolder() {
        GrooScript.convert(SOURCES_FOLDER, "$DEST_FOLDER/$BIG_JS_FILE", [classpath: SOURCES_CLASSPATH])
        assert new File("$DEST_FOLDER/$BIG_JS_FILE").exists()

        new File("$DEST_FOLDER/$BIG_JS_FILE").delete()
        new File(DEST_FOLDER).deleteDir()
    }

    void testConvertSomeFileToOneFile() {
        GrooScript.convert([new File(SOURCES_FOLDER)], new File(BIG_JS_FILE), [classpath: SOURCES_CLASSPATH])
        assert new File(BIG_JS_FILE).exists()

        cleanup:
        new File(BIG_JS_FILE).delete()
        new File(DEST_FOLDER).deleteDir()
    }

    void testConvertSomeGroovyFilesToOneFolderThatNotExists() {
        GrooScript.convert(SOURCES_FOLDER, DEST_FOLDER, [classpath: SOURCES_CLASSPATH])

        assert new File(DEST_FOLDER).exists()
        assert new File(SOURCES_FOLDER).listFiles().count { it.file } == new File(DEST_FOLDER).listFiles().count {
            it.file && it.name.endsWith('.js')
        }

        new File(DEST_FOLDER).deleteDir()
    }

    def 'not repeating js conversion options when converting to a file'() {
        given:
        def testCount = 0
        def initialTextCount = 0
        def finalTextCount = 0
        def options = [classpath: SOURCES_CLASSPATH, initialText: INITIAL, finalText: FINAL, addGsLib: 'testWithNode']
        GrooScript.convert(SOURCES_FOLDER, BIG_JS_FILE, options)

        when:
        new File(BIG_JS_FILE).eachLine { line ->
            if (line.startsWith('var gs = require(\'./grooscript.js\');')) {
                testCount++
            }
            if (line.startsWith(INITIAL)) {
                initialTextCount++
            }
            if (line.startsWith(FINAL)) {
                finalTextCount++
            }
        }

        then:
        testCount == 1
        initialTextCount == 1
        finalTextCount == 1

        cleanup:
        new File(BIG_JS_FILE).delete()
    }

    def 'evaluate js code'() {
        when:
        def testResult = GrooScript.evaluateGroovyCode('println "Hello!"')

        then:
        testResult.console == 'Hello!'
        testResult.jsCode == 'gs.println("Hello!");' + LS
        !testResult.exception
        !testResult.assertFails
    }

    def 'evaluate js code another gs lib'() {
        when:
        def testResult = GrooScript.evaluateGroovyCode('println "Hello!"', libs)

        then:
        testResult.console == 'Hello!'
        testResult.jsScript.contains 'Apache License, Version 2.0'

        where:
        libs << ['grooscript', 'grooscript, grooscript.min']
    }

    def 'convert to groovy and javascript does nothing'() {
        given:
        def data = testData

        expect:
        GrooScript.toGroovy(data) == data
        GrooScript.toJavascript(data) == data
        GrooScript.toJsObj(data) == data

        where:
        testData << [null, '', 'hello', 55, [1, 2, 3], [one: 1, two: 2], 0, false]
    }

    def 'convert function #nameFunc generates js code'() {
        given:
        def code = """
import org.grooscript.GrooScript

GrooScript.${nameFunc}('hello')
"""
        expect:
        GrooScript.convert(code) == "gs.${nameFunc}(\"hello\");" + LS

        where:
        nameFunc << ['toJavascript', 'toGroovy', 'toJsObj']
    }

    def 'convert function #nameFunc generates js code with import static'() {
        given:
        def code = """
import static org.grooscript.GrooScript.${nameFunc}

${nameFunc}('hello')
"""
        expect:
        GrooScript.convert(code) == "gs.${nameFunc}(\"hello\");" + LS

        where:
        nameFunc << ['toJavascript', 'toGroovy', 'toJsObj']
    }

    def 'show error message in console if nothing to convert'()
    {
        given:
        GroovySpy(GsConsole, global: true)

        when:
        GrooScript.convert(SOURCES_FOLDER_WITHOUT_FILES, BIG_JS_FILE)

        then:
        1 * GsConsole.error('No files to be converted. *.groovy or *.java files not found.')
    }

    def 'native code returns the string code'() {
        given:
        def data = testData

        expect:
        GrooScript.nativeJs(data) == data

        where:
        testData << ['', 'hello', null]
    }

    def 'convert to native javascript'() {
        given:
        def code = '''
import org.grooscript.GrooScript

GrooScript.nativeJs('hello')
'''
        expect:
        GrooScript.convert(code) == 'hello;' + LS
    }

    def 'convert to native javascript using static import'() {
        given:
        def code = '''
import static org.grooscript.GrooScript.nativeJs

nativeJs('hello')
'''
        expect:
        GrooScript.convert(code) == 'hello;' + LS
    }

    private static final DEST_FOLDER = 'folder'
    private static final SOURCES_CLASSPATH = 'src/test/src'
    private static final SOURCES_FOLDER = 'src/test/src/files'
    private static final SOURCES_FOLDER_WITHOUT_FILES = 'src/test/src'
    private static final BIG_JS_FILE = 'allTogether.js'
    private static final INITIAL = '// INITIALINITIAL'
    private static final FINAL = '// FINALFINAL'
}
