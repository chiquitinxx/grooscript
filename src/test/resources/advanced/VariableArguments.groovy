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

assert customSum(1, 2, 3, 4, 5) == 15
assert customSum(1, 2, 3, 4, 5) == customSum(10, 5)