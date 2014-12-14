package contribution

/**
 * Created by jorgefrancoleza on 14/12/14.
 */

int color(String name) {
    if (name == 'red')
        1
    else if (name == 'green')
        2
    else
        -1
}

assert color('red') == 1
assert color('green') == 2
assert color('yellow') == -1

boolean isInt(String s) {
    try {
        Integer.parseInt(s)
        true
    } catch (NumberFormatException e) {
        false
    } finally {
        println 'done'
    }
}

assert isInt('123')
assert !isInt('abc')