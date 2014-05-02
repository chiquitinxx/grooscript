package org.grooscript

/**
 * Created by jorge on 13/04/14.
 *
 * Generate js files
 */
class JsGenerator {

    static final HTML_BUILDER_SOURCE = 'src/main/groovy/org/grooscript/builder/HtmlBuilder.groovy'
    static final BUILDER_FILE = 'src/main/resources/META-INF/resources/grooscript-builder.js'

    static final JQUERY_SOURCE = 'src/main/groovy/org/grooscript/jquery/GQueryImpl.groovy'
    static final JQUERY_FILE = 'src/main/resources/META-INF/resources/gQueryImpl.js'

    static final BINDER_SOURCE = 'src/main/groovy/org/grooscript/jquery/Binder.groovy'
    static final BINDER_FILE = 'src/main/resources/META-INF/resources/grooscript-binder.js'

    static final GROOSCRIPT_FILE = 'src/main/resources/META-INF/resources/grooscript.js'
    static final GROOSCRIPT_ALL_FILE = 'src/main/resources/META-INF/resources/grooscript-all.js'

    static generateHtmlBuilder() {
        File source = new File(HTML_BUILDER_SOURCE)
        convertFile(source, BUILDER_FILE, [initialText: '//This script needs grooscript.js to run'])
    }

    static generateBinder() {
        File source = new File(BINDER_SOURCE)
        convertFile(source, BINDER_FILE, [initialText: '//This script needs grooscript.js and jQuery to run'])
    }

    static generateJQuery() {
        File source = new File(JQUERY_SOURCE)
        convertFile(source, JQUERY_FILE, [initialText: '//This script needs grooscript.js and jQuery to run'])
    }

    static generateGrooscriptJsComplete() {
        GrooScript.joinListOfFiles(GROOSCRIPT_FILE, BUILDER_FILE, JQUERY_FILE, BINDER_FILE, GROOSCRIPT_ALL_FILE)
    }

    static void generateAll() {
        generateHtmlBuilder()
        generateJQuery()
        generateBinder()
        generateGrooscriptJsComplete()
    }

    static convertFile(File file, String destinationFile, Map conversionOptions = [:]) {
        GrooScript.clearAllOptions()
        conversionOptions.each { key, value ->
            GrooScript.setConversionProperty(key, value)
        }
        new File(destinationFile).text = GrooScript.convert(file.text)
        GrooScript.clearAllOptions()
    }
}
