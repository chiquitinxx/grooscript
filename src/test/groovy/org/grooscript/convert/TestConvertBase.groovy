package org.grooscript.convert

import org.grooscript.GrooScript
import org.grooscript.test.JsTestResult
import spock.lang.Specification
import org.grooscript.util.Util

/**
 * Tests for converter initialization and run modes
 * JFL 27/08/12
 */
class TestConvertBase extends Specification {

    //Just started well
    def 'converter ready'() {
        when:
        def converter = new GsConverter()

        then:
        converter

        and: 'Returns null if no script passed'
        converter.toJs(null) == null
    }

    def 'conversion basic'() {
        when:
        def result = Util.fullProcessScript("println 'Trying GScript!'")

        then:
        result
        !result.assertFails
    }

    def 'full conversion results'() {
        when:
        JsTestResult result = Util.fullProcessScript("def a=0;println 'Hey';assert true")

        then:
        result.console == 'Hey'
        !result.assertFails
        result.bind.a == 0
        !result.exception
        result.jsScript == "var a = 0;${Util.LINE_SEPARATOR}gs.println(\"Hey\");${Util.LINE_SEPARATOR}" +
                "gs.assert(true, \"Assertion fails: true\");${Util.LINE_SEPARATOR}"
    }

    def 'use static class converter'() {
        when:
        def result = GrooScript.convert('def a=0')

        then:
        result == 'var a = 0;' + Util.LINE_SEPARATOR
    }
}
