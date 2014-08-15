package contribution

/**
 * Created by jorge on 15/08/14.
 */
def map = [a: 1, b: 2]

def doIt = { data ->
    data.with {
        a + b
    }
}

assert doIt(map) == 3
