package org.grooscript.jquery

import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 1/11/14
 */
class GQueryImplSpec extends Specification {

    def 'calling append'() {
        when:
        def list = gQueryImpl('p')

        then:
        list instanceof GQueryList
    }

    GQueryImpl gQueryImpl = new GQueryImpl()
}
