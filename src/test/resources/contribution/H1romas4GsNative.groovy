package contribution

/**
 * Created by jorgefrancoleza on 15/2/15.
 */

import org.grooscript.asts.GsNative

class Test1  {
    @GsNative
    def calc() {/*
        return 1
    */}
}

class Test2 {
    @GsNative
    def calc() {/*
        return 2
    */}
}

// OK
assert(new Test1().calc() == 1)
// Assertion fails: (new Test2().calc() == 2) - false
assert(new Test2().calc() == 2)