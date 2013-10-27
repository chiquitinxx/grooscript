package advanced

/**
 * User: jorgefrancoleza
 * Date: 15/07/13
 */

def touchNumbers = { a, b, c ->
    (a*b)+c
}

def touchOneTwo = touchNumbers.curry(1, 2)

assert touchOneTwo(3) == 5

def touchToFourFive = touchNumbers.rcurry(4, 5)

assert touchToFourFive(2) == 13

def touchOneTwoN = touchNumbers.ncurry(1, 5, 4)

assert touchOneTwoN(2) == 14

def otherTouch = touchNumbers.ncurry(2, 3)

assert otherTouch(10,5) == 53

def midTouch = touchNumbers.ncurry(1, 5)

assert midTouch(10,3) == 53
