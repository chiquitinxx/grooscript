package advanced

/**
 * JFL 20/12/12
 */

class C {
    def value
}

def c = new C()

c.value=5
assert c.getValue() == 5
assert c.value == 5

def number = 0

c.metaClass.getProperty = { name ->
    number++
    0
}

assert !c.value
assert number == 1

c.metaClass.getProperty = { name ->
    c.@"$name"
}

assert c.getProperty('value') == 5

class D {
    def d
    def times = 0

    def getD() {
        times++
        return d
    }

    def tellMeDValue() {
        d
    }
}

def d = new D()
d.d = 5

assert d.times == 0
assert d.d == 5
assert d.times == 1

assert d.tellMeDValue() == 5
assert d.times == 1
