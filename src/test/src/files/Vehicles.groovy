package files

/**
 * User: jorgefrancoleza
 * Date: 08/04/14
 */
def car = new Car(engine: 'cool', company: 'Groovy')
assert car.company == 'Groovy'
assert car.engine == 'cool'
assert car instanceof Vehicle
assert car instanceof Car

assert car.engineStarted == false
assert car.start() == 'Starting...'
assert car.engineStarted == true

def isStartedOut = {
    car.carStarted()
}
assert isStartedOut() == true
