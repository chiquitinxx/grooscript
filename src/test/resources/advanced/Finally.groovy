package advanced

/**
 * Created by jorgefrancoleza on 14/12/14.
 */
def value = 0

def withException = {
    try {
        throw new Exception()
    } catch (e) {
        return 2
    } finally {
        value = 1
        return 3
    }
}

assert withException() == 3
assert value == 1

def withExceptionFinallyWithoutReturn = {
    try {
        throw new Exception()
    } catch (e) {
        return 2
    } finally {
        value = 2
    }
}

assert withExceptionFinallyWithoutReturn() == 2
assert value == 2

def withFinally = {
    try {
        return 1
    } finally {
        value = 3
    }
}

assert withFinally() == 1
assert value == 3
// tag::try_finally[]
def anyCatch = {
    try {
        throw new Exception()
        value = 4
    } catch (any) {
        return 2
    }
}

assert anyCatch() == 2
assert value == 3
// end::try_finally[]