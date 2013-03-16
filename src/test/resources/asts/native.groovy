package asts

import org.grooscript.asts.GsNative

/**
 * JFL 06/11/12
 */


class Data {

    @GsNative def String saySomething(some){/*return some;*/}
    @GsNative sayOne(){/*return 1;*/}
    @GsNative public static int sayTwo(){/*return 2;*/}
    @GsNative
    static sayThree() { /*
        return 3;
    */}
    @GsNative
    def sayTrue() { /*
        return true;
    */}

    def static sayTen() {
        return 10;
    }
}

data = new Data()
//This fail in groovy, but have to work in javascript
assert data.saySomething('hello') == 'hello'
assert data.sayTrue()
assert data.sayOne() == 1
assert data.sayTwo() == 2
assert Data.sayThree() == 3
assert Data.sayTen() == 10