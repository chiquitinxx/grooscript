package contribution

/**
 * Created by jorge on 22/7/15.
 */

class SomeGroovyClass {

    def invokeMethod(String name, Object args) {
        return "called invokeMethod $name ${args.join(' ')}"
    }

    def test() {
        return 'method exists'
    }
}

def someGroovyClass = new SomeGroovyClass()

assert someGroovyClass.test() == 'method exists'
assert someGroovyClass.someMethod() == 'called invokeMethod someMethod '
assert someGroovyClass.someMethod(1, 2) == 'called invokeMethod someMethod 1 2'
