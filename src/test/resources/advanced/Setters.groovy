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

class B {
    def b
    def number

    B(value) {
        number = value
    }

    def setB(value) {
        b = value
        this.number = 5*value
    }
}

b = new B(5)
b.b = 5
assert b.b == 5
assert b.number == 25

b.setB(6)
assert b.b == 6
assert b.number == 30

//assert a.getValue() == 8
