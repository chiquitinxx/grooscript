/**
 * User: jorgefrancoleza
 * Date: 08/09/14
 */
// tag::options[]
package org.grooscript.convert

enum ConversionOptions {

    CLASSPATH('classpath', null),
    CUSTOMIZATION('customization', null),
    MAIN_CONTEXT_SCOPE('mainContextScope', null),
    INITIAL_TEXT('initialText', null),
    FINAL_TEXT('finalText', null),
    RECURSIVE('recursive', false),
    ADD_GS_LIB('addGsLib', null),
    REQUIRE_JS_MODULE('requireJsModule', false),
    CONSOLE_INFO('consoleInfo', false)

    String text
    def defaultValue

    ConversionOptions(String text, defaultValue) {
        this.text = text
        this.defaultValue = defaultValue
    }
}
// end::options[]