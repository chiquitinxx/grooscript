package advanced

/**
 * Created by jorgefrancoleza on 1/2/15.
 */

def list = [1, 2, 3, 4]

def sum = { ... values ->
    values.inject(0) { val, acc ->
        acc = acc + val
    }
}

assert sum(*list) == 10
assert sum(*list, *list) == 20
assert sum(*list, 5, *list) == 25
assert sum(12, *list) == 22
assert sum(*list, 13) == 23
