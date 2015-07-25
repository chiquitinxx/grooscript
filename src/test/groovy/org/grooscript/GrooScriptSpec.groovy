package org.grooscript

import org.grooscript.util.GsConsole
import org.grooscript.util.Util
import spock.lang.Specification
import spock.lang.Unroll

/**
 * User: jorgefrancoleza
 * Date: 08/09/14
 */
class GrooScriptSpec extends Specification {

    def 'default options'() {
        expect:
        GrooScript.defaultConversionOptions == [
                classpath: null,
                customization: null,
                mainContextScope: null,
                initialText: null,
                finalText: null,
                addGsLib: null,
                recursive: false,
                requireJsModule: false,
                consoleInfo: false,
                includeDependencies: false
        ]
    }

    @Unroll
    def 'convert some groovy files to one .js file'() {
        given:
        GrooScript.convert(SOURCES_FOLDER, destinationFile, [classpath: SOURCES_CLASSPATH])

        expect:
        new File(destinationFile).exists()

        cleanup:
        new File(destinationFile).delete()
        new File(FOLDER).deleteDir()

        where:
        destinationFile << [BIG_JS_FILE, "$FOLDER/$BIG_JS_FILE"]
    }

    @Unroll
    def 'convert some files to one file'() {
        given:
        GrooScript.convert([new File(SOURCES_FOLDER)], new File(destinationFile), [classpath: SOURCES_CLASSPATH])

        expect:
        new File(destinationFile).exists()

        cleanup:
        new File(destinationFile).delete()
        new File(FOLDER).deleteDir()

        where:
        destinationFile << [BIG_JS_FILE, "$FOLDER/$BIG_JS_FILE"]
    }

    def 'convert some groovy files to one folder that not exists'() {
        given:
        GrooScript.convert(SOURCES_FOLDER, FOLDER, [classpath: SOURCES_CLASSPATH])

        expect:
        new File(FOLDER).exists()
        new File(FOLDER).listFiles().size() == new File(SOURCES_FOLDER).listFiles().size()

        cleanup:
        new File(FOLDER).deleteDir()
    }

    def 'not repeating js conversion options when converting to a file'() {
        given:
        def jqueryLibCount = 0
        def initialTextCount = 0
        def finalTextCount = 0
        def options = [classpath: SOURCES_CLASSPATH, initialText: INITIAL, finalText: FINAL, addGsLib: 'jquery.min']
        GrooScript.convert(SOURCES_FOLDER, BIG_JS_FILE, options)

        when:
        new File(BIG_JS_FILE).eachLine { line ->
            if (line.startsWith('/*! jQuery v1.11.1')) {
                jqueryLibCount++
            }
            if (line.startsWith(INITIAL)) {
                initialTextCount++
            }
            if (line.startsWith(FINAL)) {
                finalTextCount++
            }
        }

        then:
        jqueryLibCount == 1
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
        testResult.jsCode == 'gs.println("Hello!");' + Util.LINE_SEPARATOR
        !testResult.exception
        !testResult.assertFails
    }

    @Unroll
    def 'evaluate js code another gs lib'() {
        when:
        def testResult = GrooScript.evaluateGroovyCode('println "Hello!"', libs)

        then:
        testResult.console == 'Hello!'
        testResult.jsScript.contains 'Apache 2 License'

        where:
        libs << ['grooscript', 'grooscript, grooscript.min']
    }

    @Unroll
    def 'convert to groovy and javascript does nothing'() {
        given:
        def data = testData

        expect:
        GrooScript.toGroovy(data) == data
        GrooScript.toJavascript(data) == data
        GrooScript.toJsObj(data) == data

        where:
        testData << [null, '', 'hello', 55, [1, 2, 3], [one: 1, two: 2]]
    }

    @Unroll
    def 'convert function #nameFunc generates js code'() {
        given:
        def code = """
import org.grooscript.GrooScript

GrooScript.${nameFunc}('hello')
"""
        expect:
        GrooScript.convert(code) == "gs.${nameFunc}(\"hello\");" + Util.LINE_SEPARATOR

        where:
        nameFunc << ['toJavascript', 'toGroovy', 'toJsObj']
    }

    @Unroll
    def 'convert function #nameFunc generates js code with import static'() {
        given:
        def code = """
import static org.grooscript.GrooScript.${nameFunc}

${nameFunc}('hello')
"""
        expect:
        GrooScript.convert(code) == "gs.${nameFunc}(\"hello\");" + Util.LINE_SEPARATOR

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

    @Unroll
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
        GrooScript.convert(code) == 'hello;' + Util.LINE_SEPARATOR
    }

    def 'convert to native javascript using static import'() {
        given:
        def code = '''
import static org.grooscript.GrooScript.nativeJs

nativeJs('hello')
'''
        expect:
        GrooScript.convert(code) == 'hello;' + Util.LINE_SEPARATOR
    }

    private static final FOLDER = 'folder'
    private static final SOURCES_CLASSPATH = 'src/test/src'
    private static final SOURCES_FOLDER = 'src/test/src/files'
    private static final SOURCES_FOLDER_WITHOUT_FILES = 'src/test/src'
    private static final BIG_JS_FILE = 'allTogether.js'
    private static final INITIAL = '// INITIALINITIAL'
    private static final FINAL = '// FINALFINAL'
}
