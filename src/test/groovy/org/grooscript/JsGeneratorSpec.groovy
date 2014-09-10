package org.grooscript

import spock.lang.Specification

/**
 * Created by jorge on 13/04/14.
 */
class JsGeneratorSpec extends Specification {
    def 'test generation of js files'() {
        expect:
        JsGenerator.generateGrooscriptToolsJs()
    }
}
