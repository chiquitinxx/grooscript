package contribution

import groovy.transform.Immutable

/**
 * User: jorgefrancoleza
 * Date: 27/10/13
 */
@Immutable
class Frenchy {
    String name
    int age
}

def persons = [
    new Frenchy('Guillaume', 36),
    new Frenchy('Marion', 5),
    new Frenchy('Erine', 1)
]

def names = persons.findAll { it.age < 18}
        .collect { it.name.toUpperCase() }
        .sort()
        .join(', ')

assert names == "ERINE, MARION"
