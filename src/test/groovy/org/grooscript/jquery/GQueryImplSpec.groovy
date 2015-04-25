package org.grooscript.jquery

import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 1/11/14
 */
class GQueryImplSpec extends Specification {

    def 'bind all properties'() {
        given:
        def item = new Expando(name: 'name', id: 'id', group: 'group')

        expect:
        gQueryImpl.bindAllProperties(item)
    }

    def 'calling append'() {
        when:
        def list = gQueryImpl('p')

        then:
        list instanceof GQueryList
    }

    GQueryImpl gQueryImpl = new GQueryImpl()
}
