package advanced

/**
 * User: jorgefrancoleza
 * Date: 27/10/13
 */

def customSum = { ... elements ->
    elements.inject(0) { val, acc ->
        acc + val
    }
}

assert customSum() == 0
assert customSum(5) == 5
assert customSum(1, 2, 3, 4, 5) == 15
assert customSum(1, 2, 3, 4, 5) == customSum(10, 5)
def list = [1, 2, 3, 4, 5] as Object[]
assert customSum(list) == 15