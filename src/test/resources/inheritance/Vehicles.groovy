package inheritance

/**
 * User: jorgefrancoleza
 * Date: 08/04/14
 */
import inheritance.Car

def car = new Car(engine: 'cool', company: 'Groovy')
assert car.company == 'Groovy'
assert car.engine == 'cool'
