package org.grooscript

import org.grooscript.util.GsConsole

/**
 * Created by jorgefrancoleza on 22/5/15.
 */
class JsGenerator {

    static final HTML_BUILDER_SOURCE = 'src/main/groovy/org/grooscript/builder/HtmlBuilder.groovy'
    static final BUILDER_FILE = 'src/main/resources/META-INF/resources/grooscript-builder.js'

    static final JQUERY_SOURCE = 'src/main/groovy/org/grooscript/jquery/GQueryImpl.groovy'
    static final JQUERY_FILE = 'src/main/resources/META-INF/resources/gQueryImpl.js'

    static final OBSERVABLE_SOURCE = 'src/main/groovy/org/grooscript/rx/Observable.groovy'
    static final OBSERVABLE_FILE = 'src/main/resources/META-INF/resources/observable.js'

    static final GROOSCRIPT_TOOLS_FILE = 'src/main/resources/META-INF/resources/grooscript-tools.js'

    static void generateGrooscriptToolsJs() {
        generateHtmlBuilder()
        generateJQuery()
        generateObservable()
        generateGrooscriptJsToolsComplete()
    }

    private static void generateHtmlBuilder() {
        File source = new File(HTML_BUILDER_SOURCE)
        convertFile(source, BUILDER_FILE)
    }

    private static void generateObservable() {
        File source = new File(OBSERVABLE_SOURCE)
        convertFile(source, OBSERVABLE_FILE)
    }

    private static void generateJQuery() {
        File source = new File(JQUERY_SOURCE)
        convertFile(source, JQUERY_FILE)
    }

    private static void generateGrooscriptJsToolsComplete() {
        GrooScript.joinListOfFiles(BUILDER_FILE, OBSERVABLE_FILE, JQUERY_FILE, GROOSCRIPT_TOOLS_FILE)
        GsConsole.info("File $GROOSCRIPT_TOOLS_FILE has been generated.")
    }

    private static void convertFile(File file, String destinationFile, Map conversionOptions = null) {
        GrooScript.clearAllOptions()
        GrooScript.options = conversionOptions ?: GrooScript.defaultOptions
        new File(destinationFile).text = GrooScript.convert(file.text)
        GrooScript.clearAllOptions()
    }
}

JsGenerator.generateGrooscriptToolsJs()
