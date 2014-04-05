package org.grooscript.util

import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 05/04/14
 */
class TestUtil extends Specification {

    def 'test actual version'() {
        expect:
        Util.groovyVersionAtLeast('2.0')
    }
}
