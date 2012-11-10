package advanced

/**
 * JFL 10/11/12
 */

def list = [1,3,7,4,9,2,5]
//False don't modify original list
assert list.sort(false) == [1,2,3,4,5,7,9]

assert list == [1,3,7,4,9,2,5]

assert list.sort() == [1,2,3,4,5,7,9]
assert list == [1,2,3,4,5,7,9]

/* Not comparator param
class Comp implements Comparator {

    def int compare (o1, o2) {
        o2-o1
    }
    def boolean equals(other) {
        this.equals(other)
    }
}

assert list.sort(false,new Comp()) == [9, 7, 5, 4, 3, 2, 1]
*/

assert ["hi","hey","hello"] == ["hello","hi","hey"].sort { it.size() }

assert ["hi","hey","hello"] == ["hello","hi","hey"].sort { a, b -> a.size() <=> b.size() }

def orig = ["hello","hi","Hey"]

def sorted = orig.sort(false) { it.toUpperCase() }

assert orig == ["hello","hi","Hey"]

assert sorted == ["hello","Hey","hi"]