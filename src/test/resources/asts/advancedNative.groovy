package asts

import org.grooscript.asts.GsNative

/**
 * JFL 10/10/13
 */
class Foo {
    def a = 0
    public void methodA(){
        println 'methodA'
        a++
    }

    @GsNative
    def methodB(){/*
            gs.println('methodB');
            this.a++;
            this.methodA();
    */ a = a + 1; println 'inMethodB'; this.methodA()}

    @GsNative
    static bar() {/*
        return 'bar';
    */ println 'inStatic'; 'bar'}
}

def foo = new Foo()
foo.methodB()

assert foo.a == 2
assert Foo.bar() == 'bar'