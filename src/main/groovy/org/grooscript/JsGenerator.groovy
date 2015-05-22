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

    static final OBSERVABLE_SOURCE = 'src/main/groovy/org/grooscript/rx/Observable.groovy'
    static final OBSERVABLE_FILE = 'src/main/resources/META-INF/resources/observable.js'

    static final GROOSCRIPT_TOOLS_FILE = 'src/main/resources/META-INF/resources/grooscript-tools.js'

    static void generateHtmlBuilder() {
        File source = new File(HTML_BUILDER_SOURCE)
        convertFile(source, BUILDER_FILE)
    }

    static void generateObservable() {
        File source = new File(OBSERVABLE_SOURCE)
        convertFile(source, OBSERVABLE_FILE)
    }

    static void generateJQuery() {
        File source = new File(JQUERY_SOURCE)
        convertFile(source, JQUERY_FILE)
    }

    static void generateGrooscriptJsToolsComplete() {
        GrooScript.joinListOfFiles(BUILDER_FILE, OBSERVABLE_FILE, JQUERY_FILE, GROOSCRIPT_TOOLS_FILE)
    }

    static void generateGrooscriptToolsJs() {
        generateHtmlBuilder()
        generateJQuery()
        generateObservable()
        generateGrooscriptJsToolsComplete()
    }

    static void convertFile(File file, String destinationFile, Map conversionOptions = null) {
        GrooScript.clearAllOptions()
        GrooScript.options = conversionOptions ?: GrooScript.defaultOptions
        new File(destinationFile).text = GrooScript.convert(file.text)
        GrooScript.clearAllOptions()
    }
}
