package classes

/**
 * User: jorgefrancoleza
 * Date: 07/08/13
 */

abstract class DefaultCar {
    public abstract int start()
}

class MyCar extends DefaultCar {
    public int start() {
        5
    }
}

assert new MyCar().start() == 5


