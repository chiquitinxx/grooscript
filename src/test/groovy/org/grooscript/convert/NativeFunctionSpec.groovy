package org.grooscript.convert

import spock.lang.Specification

/**
 * Created by jorgefrancoleza on 7/8/15.
 */
class NativeFunctionSpec extends Specification {

    def 'native function to string'() {
        expect:
        new NativeFunction(className: 'cn', methodName: 'mn').toString() == 'cn - mn'
    }
}
