package contribution

/**
 * User: jorgefrancoleza
 * Date: 05/04/14
 */

def square = { it * it }
def plusOne = { it + 1 }
def half = { it / 2 }

def c = half >> plusOne >> square

assert c(10) == 36

def d = half << plusOne << square

assert d(10) == 50.5