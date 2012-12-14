package advanced

/**
 * JFL 14/12/12
 */

class A {
    def value
}

def a = new A()

a.value=5
assert a.value==5

a.setValue(6)
assert a.value == 6

def number = 0

a.metaClass.setProperty = { name,value ->
    number++
}

a.value = 7
assert number == 1
assert a.value == 6

a.metaClass.setProperty = { name,value ->
    a.@"$name" = value
}

a.value = 8
assert number == 1
assert a.value == 8


assert a.getValue() == 8
