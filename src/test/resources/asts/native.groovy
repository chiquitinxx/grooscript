package asts

import org.grooscript.asts.GsNative

/**
 * JFL 06/11/12
 */


class Data {

    @GsNative def String saySomething(some){/*return some;*/}
    @GsNative
    def sayTrue() { /*
        return true;
    */}
}

data = new Data()
//This fail in groovy, but have to work in javascript
assert data.sayTrue()