package classes

/**
 * User: jorgefrancoleza
 * Date: 03/04/14
 */
// tag::mark[]
final class Distance {
    def number
    String toString() { "${number}m" }
}

class NumberCategory {
    static Distance getMeters(Number self) {
        new Distance(number: self)
    }
}

use(NumberCategory) {
    def dist = 300.meters

    assert dist instanceof Distance
    assert dist.toString() == "300m"
}
// end::mark[]
