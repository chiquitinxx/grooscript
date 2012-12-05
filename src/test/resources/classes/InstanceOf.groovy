package classes

/**
 * JFL 05/12/12
 */

class A {

}

class B extends A {

}

b = new B()

assert new A() instanceof A
assert b instanceof B
assert b instanceof A

assert "1" instanceof String
assert !("1" instanceof Number)
assert 2 instanceof Number
assert 3.4 instanceof Number
