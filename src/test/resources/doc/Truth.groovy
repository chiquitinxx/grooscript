package doc

/**
 * Created by jorgefrancoleza on 21/12/14.
 */
// tag::truth[]
def a = true
def b = true
def c = false
assert a
assert a && b
assert a || c
assert !c

def numbers = [1,2,3]
assert numbers
numbers = []
assert !numbers

assert ['one':1]
assert ![:]

assert ('Hello World' =~ /World/)

assert 'This is true'
assert !''

def s = ''
assert !("$s")
s = 'x'
assert ("$s")

assert !0
assert 1

assert new Object()
assert !null
// end::truth[]
