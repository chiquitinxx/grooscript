package traits

/**
 * Created by jorgefrancoleza on 8/1/15.
 */

trait Data {
    static ONE = 1
    static TWO = 2
    static ALL = [ONE, TWO]
}

class Numbers implements Data {}
assert Numbers.ALL == [1, 2]