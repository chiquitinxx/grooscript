package org.grooscript

import org.grooscript.test.ConversionMixin
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 22/11/13
 */
@Mixin([ConversionMixin])
class TestTraits extends Specification {

    def 'initial traits support'() {
        expect:
        !readAndConvert('traits/Starting').assertFails
    }
}
