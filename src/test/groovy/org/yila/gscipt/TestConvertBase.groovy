package org.yila.gscipt

import spock.lang.Specification
import org.yila.gscript.GsConverter

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


}
