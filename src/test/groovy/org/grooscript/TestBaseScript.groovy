package org.grooscript

import org.grooscript.test.ConversionMixin
import spock.lang.Specification

/**
 * Created by jorge on 27/07/14.
 */
@Mixin([ConversionMixin])
class TestBaseScript extends Specification {

    void 'initial base script'() {
        expect:
        convertAndEvaluate('baseScript/initial', true, [consoleInfo: true])
    }
}
