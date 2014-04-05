package classes

/**
 * User: jorgefrancoleza
 * Date: 04/04/14
 */

class Doing {
    def call() {
        return 5
    }
}

def doing = new Doing()
assert doing.call() == 5
assert doing() == 5

assert new Doing()() == 5