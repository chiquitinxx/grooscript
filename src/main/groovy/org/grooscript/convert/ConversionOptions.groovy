package org.grooscript.convert

/**
 * User: jorgefrancoleza
 * Date: 08/09/14
 */
enum ConversionOptions {

    CLASSPATH('classPath', null),
    DEPENDENCIES('convertDependencies', false),
    CUSTOMIZATION('customization', null),
    MAIN_CONTEXT_SCOPE('mainContextScope', null),
    INITIAL_TEXT('initialText', null),
    FINAL_TEXT('finalText', null),
    RECURSIVE('recursive', false),
    INCLUDE_JS_LIB('includeJsLib', null)

    String text
    def defaultValue

    ConversionOptions(String text, defaultValue) {
        this.text = text
        this.defaultValue = defaultValue
    }
}
