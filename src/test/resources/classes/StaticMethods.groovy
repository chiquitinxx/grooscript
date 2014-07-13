package classes

/**
 * User: jorgefrancoleza
 * Date: 13/07/14
 */

class WithStaticMethods {
    static list = ['me', 'groovy']

    static sayHello() {
        def result = []
        list.each {
            result << clone(it)
        }
    }

    static clone(item) {
        item
    }
}

def list = []
WithStaticMethods.sayHello().each {
    list << it.toUpperCase()
}

assert list == ['ME', 'GROOVY']