package traits

/**
 * Created by jorgefrancoleza on 28/12/14.
 */

trait StaticFields {
    static VALUE = 0
    static LIST = [1, 2]
    static EMPTY
    static EMPTY_2
}

class WithStaticFields implements StaticFields {
    def add() {
        EMPTY_2 = 1
        LIST << VALUE
    }
}

assert WithStaticFields.VALUE == 0
assert WithStaticFields.LIST == [1, 2]
assert WithStaticFields.EMPTY == null
assert WithStaticFields.EMPTY_2 == null
assert new WithStaticFields().add() == [1, 2, 0]
assert WithStaticFields.EMPTY == null
assert WithStaticFields.EMPTY_2 == 1
