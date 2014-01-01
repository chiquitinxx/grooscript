package advanced

/**
 * User: jorgefrancoleza
 * Date: 29/12/13
 */
def cl1 = { a ->
    a
}

def cl2 = { a ->
    a > 0 ? a : 0
}

def cl3 = { a ->
    if (a > 0) {
        a
    } else {
        0
    }
}

def cl4 = { a ->
    if (a > 0) a
    else 0
}

def cl5 = { a ->
    if (a > 0) {
        if (a > 5) a
        else 5
    } else {
        0
    }
}

def cl6 = { a ->
    if (a > 0) {
        if (a > 3) {
            3
            4
        } else {
            5
        }
        return 1
    } else {
        0
    }
}

assert cl1(1) == 1
assert cl2(1) == 1
assert cl2(-1) == 0
assert cl3(1) == 1
assert cl3(-1) == 0
assert cl4(1) == 1
assert cl4(-1) == 0
assert cl5(1) == 5
assert cl5(9) == 9
assert cl5(-1) == 0
assert cl6(5) == 1
assert cl6(-1) == 0
