package classes

/**
 * User: jorgefrancoleza
 * Date: 31/03/14
 */

class Engine {
    def cv
    def start() {
        'starting...'
    }
}

class SuperCar {
    @Delegate Engine engine
}

def superCar = new SuperCar(engine: new Engine(cv: 100))

assert superCar.start() == 'starting...'
assert superCar.cv == 100
