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

    static final GROOSCRIPT_TOOLS_FILE = 'src/main/resources/META-INF/resources/grooscript-tools.js'

    static generateHtmlBuilder() {
        File source = new File(HTML_BUILDER_SOURCE)
        convertFile(source, BUILDER_FILE, [initialText: '//This script needs grooscript.js to run'])
    }

    static generateJQuery() {
        File source = new File(JQUERY_SOURCE)
        convertFile(source, JQUERY_FILE, [initialText: '//This script needs grooscript.js and jQuery to run'])
    }

    static generateGrooscriptJsToolsComplete() {
        GrooScript.joinListOfFiles(BUILDER_FILE, JQUERY_FILE, GROOSCRIPT_TOOLS_FILE)
    }

    static void generateGrooscriptToolsJs() {
        generateHtmlBuilder()
        generateJQuery()
        generateGrooscriptJsToolsComplete()
    }

    static convertFile(File file, String destinationFile, Map conversionOptions = null) {
        GrooScript.clearAllOptions()
        GrooScript.options = conversionOptions ?: GrooScript.defaultOptions
        new File(destinationFile).text = GrooScript.convert(file.text)
        GrooScript.clearAllOptions()
    }
}
