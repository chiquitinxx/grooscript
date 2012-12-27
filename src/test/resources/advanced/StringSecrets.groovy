package advanced

/**
 * JFL 22/12/12
 */

v1 = 'alpha'; v2 = 'omega'
// this can done with explicit swapping via a temp variable
// or in a slightly more interesting way with a closure
swap = { temp = v1; v1 = v2; v2 = temp }
swap()
assert v1 == 'omega' && v2 == 'alpha'

assert 'string'.reverse() == 'gnirts'

string = 'Yoda said, "can you see this?"'
revwords = string.split(' ').toList().reverse().join(' ')
assert revwords == 'this?" see you "can said, Yoda'

words = ['bob', 'alpha', 'rotator', 'omega', 'reviver']
long_palindromes = words.findAll{ w -> w == w.reverse() && w.size() > 5 }
assert long_palindromes == ['rotator', 'reviver']
//Javascript stuff
//assert "%22" == "\""
assert "hello".length() == 5