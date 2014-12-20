package doc

/**
 * Created by jorgefrancoleza on 20/12/14.
 */
// tag::operators[]
def compare = { a,b -> a <=> b }
assert compare(2, 1) == 1 && compare(1, 1) == 0 && compare(1, 2) == -1

class Item {
    def value
    def config = [:]
    def getValue() {
        1
    }
    boolean equals(other) {
        this.config == other.config
    }
}
def item = new Item(value: 5)
assert item.value == 1
assert item.@value == 5

assert 5 in [1, 9, 5]
assert !(2 in [1])

def otherItem = new Item(value: 5)
assert item == otherItem
assert !(item.is(otherItem))

assert item?.config?.other == null
assert 5 == item?.config?.data ?: 5
// end::operators[]