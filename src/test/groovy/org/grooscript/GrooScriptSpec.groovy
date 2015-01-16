package org.grooscript

import org.grooscript.convert.ConversionOptions
import org.grooscript.util.GsConsole
import org.grooscript.util.Util
import spock.lang.Specification
import spock.lang.Unroll

/**
 * User: jorgefrancoleza
 * Date: 08/09/14
 */
class GrooScriptSpec extends Specification {

    def 'conversion options'() {
        expect:
        ConversionOptions.values().size() == 7
    }

    def 'default options'() {
        expect:
        GrooScript.defaultOptions == [
            classPath: null,
            customization: null,
            mainContextScope: null,
            initialText: null,
            finalText: null,
            addGsLib: null,
            recursive: false,
        ]
    }

    @Unroll
    def 'convert some groovy files to one .js file'() {
        given:
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, SOURCES_CLASSPATH)
        GrooScript.convert(SOURCES_FOLDER, destinationFile)

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
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, SOURCES_CLASSPATH)
        GrooScript.convert([new File(SOURCES_FOLDER)], new File(destinationFile))

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
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, SOURCES_CLASSPATH)
        GrooScript.convert(SOURCES_FOLDER, FOLDER)

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
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, SOURCES_CLASSPATH)
        GrooScript.setConversionProperty(ConversionOptions.INITIAL_TEXT.text, INITIAL)
        GrooScript.setConversionProperty(ConversionOptions.FINAL_TEXT.text, FINAL)
        GrooScript.setConversionProperty(ConversionOptions.ADD_GS_LIB.text, 'jquery.min')
        GrooScript.convert(SOURCES_FOLDER, BIG_JS_FILE)

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

        where:
        testData << [null, '', 'hello', 55, [1, 2, 3], [one: 1, two: 2]]
    }

    def 'convert to javascript generates js code'() {
        given:
        def code = '''
import org.grooscript.GrooScript

GrooScript.toJavascript('hello')
'''
        expect:
        GrooScript.convert(code) == 'gs.toJavascript("hello");' + Util.LINE_SEPARATOR
    }

    def 'convert to groovy generates js code'() {
        given:
        def code = '''
import static org.grooscript.GrooScript.toGroovy

toGroovy('hello')
'''
        expect:
        GrooScript.convert(code) == 'gs.toGroovy("hello");' + Util.LINE_SEPARATOR
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

    def setup() {
        GrooScript.clearAllOptions()
    }

    private static final FOLDER = 'folder'
    private static final SOURCES_CLASSPATH = 'src/test/src'
    private static final SOURCES_FOLDER = 'src/test/src/files'
    private static final SOURCES_FOLDER_WITHOUT_FILES = 'src/test/src'
    private static final BIG_JS_FILE = 'allTogether.js'
    private static final INITIAL = '// INITIALINITIAL'
    private static final FINAL = '// FINALFINAL'
}
