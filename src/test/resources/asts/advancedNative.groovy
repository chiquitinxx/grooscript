package asts

import org.grooscript.asts.GsNative

/**
 * JFL 10/10/13
 */
def a = 0

class Foo {
    public void methodA(){
        println 'methodA'
        a = a + 1
    }

    @GsNative
    def methodB(){/*
            gs.println('methodB');
            a++;
            this.methodA();
    */}
}

new Foo().methodB()
//This assert only works when converted
assert a == 2