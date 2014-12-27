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

    static final JQUERY_TRAIT_SOURCE = 'src/main/groovy/org/grooscript/jquery/GQueryTrait.groovy'
    static final JQUERY_TRAIT_FILE = 'src/main/resources/META-INF/resources/gQueryTrait.js'

    static final OBSERVABLE_SOURCE = 'src/main/groovy/org/grooscript/rx/Observable.groovy'
    static final OBSERVABLE_FILE = 'src/main/resources/META-INF/resources/observable.js'

    static final GROOSCRIPT_TOOLS_FILE = 'src/main/resources/META-INF/resources/grooscript-tools.js'

    static generateHtmlBuilder() {
        File source = new File(HTML_BUILDER_SOURCE)
        convertFile(source, BUILDER_FILE)
    }

    static generateObservable() {
        File source = new File(OBSERVABLE_SOURCE)
        convertFile(source, OBSERVABLE_FILE)
    }

    static generateJQuery() {
        File source = new File(JQUERY_SOURCE)
        convertFile(source, JQUERY_FILE)
    }

    static generateJQueryTrait() {
        File source = new File(JQUERY_TRAIT_SOURCE)
        convertFile(source, JQUERY_TRAIT_FILE)
    }

    static generateGrooscriptJsToolsComplete() {
        GrooScript.joinListOfFiles(BUILDER_FILE, OBSERVABLE_FILE, JQUERY_FILE, JQUERY_TRAIT_FILE, GROOSCRIPT_TOOLS_FILE)
    }

    static void generateGrooscriptToolsJs() {
        generateHtmlBuilder()
        generateJQuery()
        generateJQueryTrait()
        generateObservable()
        generateGrooscriptJsToolsComplete()
    }

    static convertFile(File file, String destinationFile, Map conversionOptions = null) {
        GrooScript.clearAllOptions()
        GrooScript.options = conversionOptions ?: GrooScript.defaultOptions
        new File(destinationFile).text = GrooScript.convert(file.text)
        GrooScript.clearAllOptions()
    }
}
