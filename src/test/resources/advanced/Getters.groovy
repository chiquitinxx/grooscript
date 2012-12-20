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
