package org.grooscript

import org.grooscript.convert.ConversionOptions
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
            includeJsLib: null,
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

        and:
        new File(destinationFile).delete()

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
        def jsLibCount = 0
        def initialTextCount = 0
        def finalTextCount = 0
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, SOURCES_CLASSPATH)
        GrooScript.setConversionProperty(ConversionOptions.INITIAL_TEXT.text, INITIAL)
        GrooScript.setConversionProperty(ConversionOptions.FINAL_TEXT.text, FINAL)
        GrooScript.setConversionProperty(ConversionOptions.INCLUDE_JS_LIB.text, 'grooscript-tools')
        GrooScript.convert(SOURCES_FOLDER, BIG_JS_FILE)

        when:
        new File(BIG_JS_FILE).eachLine { line ->
            if (line.startsWith('function HtmlBuilder() {')) {
                jsLibCount++
            }
            if (line.startsWith(INITIAL)) {
                initialTextCount++
            }
            if (line.startsWith(FINAL)) {
                finalTextCount++
            }
        }

        then:
        jsLibCount == 1
        initialTextCount == 1
        finalTextCount == 1

        cleanup:
        new File(BIG_JS_FILE).delete()
    }

    def setup() {
        JsGenerator.generateGrooscriptToolsJs()
    }

    private static final FOLDER = 'folder'
    private static final SOURCES_CLASSPATH = 'src/test/src'
    private static final SOURCES_FOLDER = 'src/test/src/files'
    private static final BIG_JS_FILE = 'allTogether.js'
    private static final INITIAL = '// INITIALINITIAL'
    private static final FINAL = '// FINALFINAL'
}
