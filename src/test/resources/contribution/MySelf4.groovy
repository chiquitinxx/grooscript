package contribution

/**
 * Created by jorge on 28/07/14.
 */
class Const {
    static final FIVE = 5
    static doubleMe = { value ->
        value * 2
    }
}

import static contribution.Const.*

def value = 1

contribution.Const.FIVE.times {
    value = contribution.Const.doubleMe value
}

assert doubleMe(FIVE) == 10

assert value == 32

assert (1..FIVE).collect { it * 2 }.sum() == 30
