package traits

import org.grooscript.asts.GsNative

/**
 * User: jorgefrancoleza
 * Date: 05/04/14
 */

trait NumberAbility {
    @GsNative
    int giveMeFive() {/*
        return 5;
    */}

    @GsNative
    int doubleMe(num) {/*
        return num * 2;
    */}
}

class MyNative implements NumberAbility {}
def myNative = new MyNative()

assert myNative.giveMeFive() == 5
assert myNative.doubleMe(7) == 14

