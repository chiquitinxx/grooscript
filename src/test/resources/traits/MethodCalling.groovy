package traits

/**
 * Created by jorgefrancoleza on 30/12/14.
 */

trait Methods {
    static one() {
        1
    }
    static ONE = 1
    static two(other) {
        ONE = ONE + 1
        [ONE, one(), other]
    }
    def three() {
        3
    }
    def five = 5
    def four() {
        [three(), one(), five]
    }
}

class WithMethods implements Methods {
    def init() {
        two(4) + four()
    }
}

assert WithMethods.one() == 1
assert WithMethods.two(2) == [2, 1, 2]
def withMethods = new WithMethods()
assert withMethods.init() == [3, 1, 4, 3, 1, 5]
