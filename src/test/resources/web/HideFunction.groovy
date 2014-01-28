package web

/**
 * User: jorgefrancoleza
 * Date: 28/01/14
 */
def add = { a ->
    bValue() + a
}

//Only works in test with replacement
//Not working in Node.js
assert add(5) == 9

class Adding {
    def add(a) {
        bValue() + a
    }
}

//Not working in Node.js
assert new Adding().add(5) == 9