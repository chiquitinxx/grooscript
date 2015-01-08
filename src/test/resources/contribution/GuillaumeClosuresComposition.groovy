package contribution

/**
 * User: jorgefrancoleza
 * Date: 27/10/13
 */
// tag::mark[]
def plus2 = { it + 2}
def times3 = { it * 3}

def times3plus2 = plus2 << times3
assert times3plus2(3) == 11
assert times3plus2(4) == plus2(times3(4))

def plus2times3 = times3 << plus2
assert plus2times3(3) == 15
assert plus2times3(5) == times3(plus2(5))

assert times3plus2(3) == (times3 >> plus2)(3)
// end::mark[]