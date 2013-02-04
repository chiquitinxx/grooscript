package advanced

/**
 * User: jorgefrancoleza
 * Date: 04/02/13
 */

class Who {
    def demo
}

def who = new Who()

def result = who?.demo?.goal
assert result == null
assert who.demo == null

def a = 5

if (who?.demo) {
    a = 6
}

assert a == 5
