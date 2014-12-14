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