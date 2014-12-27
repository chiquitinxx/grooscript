package org.grooscript.jquery

import spock.lang.Specification

/**
 * Created by jorge on 27/12/14.
 */
class GQueryTraitSpec extends Specification {

    void 'class with trait has gQuery'() {
        expect:
        objectWithTrait.gQuery instanceof GQueryImpl
    }

    def objectWithTrait = new Object() as GQueryTrait
}
