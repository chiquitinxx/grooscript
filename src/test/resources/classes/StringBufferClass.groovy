package classes

/**
 * User: jorgefrancoleza
 * Date: 21/02/13
 */

def text = new StringBuffer()

assert text!=null
assert new StringBuffer('hola').toString() == 'hola'

text << 'hello!'
assert text.toString() == 'hello!'
println text
