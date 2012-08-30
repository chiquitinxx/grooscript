/**
 * JFL 30/08/12
 */
def a
b = 3.2
c = 'Hello!'

assert 5!=b
a = 5
assert a > b
assert a == 5
assert c+" ${a}" == 'Hello! 5'
assert !false
assert (5*2)-3 == 7
assert 5*(2-3) == -5