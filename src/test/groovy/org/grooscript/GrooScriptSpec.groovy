package org.grooscript

import org.grooscript.convert.ConversionOptions
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 08/09/14
 */
class GrooScriptSpec extends Specification {

    def 'conversion options'() {
        expect:
        ConversionOptions.values().size() == 8
    }

    def 'default options'() {
        expect:
        GrooScript.defaultOptions == [
            classPath: null,
            convertDependencies: false,
            customization: null,
            mainContextScope: null,
            initialText: null,
            finalText: null,
            includeJsLib: null,
            recursive: false,
        ]
    }
}
