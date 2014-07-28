package org.grooscript

import org.grooscript.test.ConversionMixin
import org.grooscript.util.Util
import spock.lang.IgnoreIf
import spock.lang.Specification

/**
 * Created by jorge on 27/07/14.
 */
@Mixin([ConversionMixin])
@IgnoreIf({ !Util.groovyVersionAtLeast('2.2') })
class TestBaseScript extends Specification {

    void 'initial base script'() {
        expect:
        convertAndEvaluate('baseScript/initial')
    }
}
