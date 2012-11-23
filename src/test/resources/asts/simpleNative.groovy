package asts

import org.grooscript.asts.GsNative

/**
 * JFL 19/11/12
 */

class Numbers {

    def getFour() {
        4
    }

    /* A comment */

    @GsNative
    def getFive() {/*
        //Comment
        return 5;
    */}

    /* Other comment */

    def getSix() {
        6
    }
}

numbers = new Numbers()

//Only works in javascript
assert numbers.getFive() == 5
