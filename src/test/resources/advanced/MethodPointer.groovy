package advanced

/**
 * User: jorgefrancoleza
 * Date: 01/02/13
 */

class Funcs {

    def value = 2
    static valueStatic = 5

    public five() {
        return 5
    }

    def giveFive() {
        return this.&five
    }

    def plus(a=value,b=valueStatic) {
        a+b
    }

    def giveMePlus() {
        return this.&plus
    }
}

def funcs = new Funcs()
def giveMeFive = funcs.&five

assert giveMeFive() == 5
assert funcs.giveFive()() == 5
assert funcs.giveFive().call() == 5

assert funcs.giveMePlus()(6,3) == 9
funcs.value = 7
assert funcs.giveMePlus()() == 12