package advanced

/**
 * User: jorgefrancoleza
 * Date: 15/07/13
 */

def touchNumbers = { a, b, c ->
    (a*b)+c
}

def touchOneTwo = touchNumbers.curry(1,2)

assert touchOneTwo(3) == 5

def touchToFourFive = touchNumbers.rcurry(4,5)

assert touchToFourFive(2) == 13
