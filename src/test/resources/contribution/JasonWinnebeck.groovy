package contribution

/**
 * User: jorgefrancoleza
 * Date: 17/09/14
 */

interface Y extends YParent {
}

interface YParent {
    int getValue()
}

@groovy.transform.CompileStatic
def check() {
    Y y = null
    return y?.value
}

//A groovy bug!
assert check() == null
