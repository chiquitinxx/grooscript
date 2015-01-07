package traits

/**
 * Created by jorgefrancoleza on 28/12/14.
 */

trait StaticFields {
    static VALUE = 0
    static LIST = [1, 2]
    static EMPTY
    static EMPTY_2

    static initStatic() {
        VALUE = 0
        LIST = [1, 2]
    }

    def init() {
        VALUE = 0
        LIST = [1, 2]
        EMPTY = null
    }
}

trait StaticColors {
    static COLORS = ['red', 'blue']
}

class WithStaticFields implements StaticFields, StaticColors {
    def add() {
        EMPTY_2 = 1
        LIST << VALUE
    }
}

def checkAll = {
    assert WithStaticFields.VALUE == 0
    assert WithStaticFields.LIST == [1, 2]
    assert WithStaticFields.EMPTY == null
    assert new WithStaticFields().add() == [1, 2, 0]
    assert WithStaticFields.EMPTY == null
    assert WithStaticFields.EMPTY_2 == 1
    assert WithStaticFields.COLORS == ['red', 'blue']
}
checkAll()
WithStaticFields.initStatic()
checkAll()
def withStaticFields = new WithStaticFields()
withStaticFields.init()
checkAll()

