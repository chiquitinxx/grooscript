package advanced

/**
 * User: jorgefrancoleza
 * Date: 12/05/14
 */

class WithBoolean {
    boolean data = true
    boolean value = false
}

def withBoolean = new WithBoolean()

assert withBoolean.isData() == true
assert withBoolean.isValue() == false
