package org.yila.gscript

import spock.lang.Specification
import org.yila.gscript.GsConverter
import org.yila.gscript.util.Util

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
        //Returns null if no script passed
        !converter.toJs()
    }

    def 'full conversion results'() {
        when:
        def result = Util.fullProcessScript("def a=0;println 'Hey';assert true")

        then:
        result instanceof Map
        result.gSconsole == 'Hey'
        !result.assertFails
        result.a == 0
        !result.exception
        result.jsScript == 'var a = 0;\ngSprintln("Hey");\ngSassert(true, null);\n'
    }


}
