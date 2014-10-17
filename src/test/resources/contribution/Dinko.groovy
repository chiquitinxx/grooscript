package contribution

/**
 * User: jorgefrancoleza
 * Date: 17/10/14
 */

def a = 5

assert a == 5
assert 3 == ( a = 3 )
assert a == 3

trait TraitA {
    def foo
    def getBar() { foo = 42 }
}

class ClassC implements TraitA {}

def classc = new ClassC()
assert classc.bar == 42 // In groovy returns null, a bug in groovy 2.3.7
assert classc.foo == 42
