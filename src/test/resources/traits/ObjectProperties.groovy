package traits

/**
 * Created by jorgefrancoleza on 1/3/15.
 */

trait TraitWithProps {
    static one
    def two
}

class MyObjectWithTrait implements TraitWithProps {
    def three
    static four
}

def object = new MyObjectWithTrait()
assert object.properties.size() == 5
assert object.properties.findAll { it.key == 'pepe' } == [:]
assert object.properties.findAll { it.key == 'class' }.collect { it.key } == ['class']
assert object.properties.findAll { it.key == 'one' }.collect { it.key } == ['one']
assert object.properties.findAll { it.key == 'two' }.collect { it.key } == ['two']
assert object.properties.findAll { it.key == 'three' }.collect { it.key } == ['three']
assert object.properties.findAll { it.key == 'four' }.collect { it.key } == ['four']
