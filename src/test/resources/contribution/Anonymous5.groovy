package contribution

/**
 * User: jorgefrancoleza
 * Date: 30/01/14
 */
def b = (1..10).findAll({ x -> x % 2 == 0 }).collect({ x -> x**2 })
assert b == [4, 16, 36, 64, 100]
assert 5**2 == 25
assert 5**3 == 125