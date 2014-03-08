package contribution

/**
 * User: jorgefrancoleza
 * Date: 21/02/14
 */
class Phone {
    def number

    boolean equals(Object o) {
        number == o.number
    }
}

def myPhone = new Phone(number: 987654321)
def mySamePhone = myPhone
def oldPhone = new Phone(number: 987654321)

assert mySamePhone == myPhone
assert myPhone.is(mySamePhone)
assert mySamePhone.is(myPhone)

assert mySamePhone == oldPhone
assert !myPhone.is(oldPhone)
assert !oldPhone.is(myPhone)

class RedColor {
    def is(...values) {
        values.any { String value ->
            value.toUpperCase() == 'RED'
        }
    }
}

def red = new RedColor()
assert red.is('blue', 'red')
assert red.is('black', 'brown', 'red')
assert !red.is('black', 'brown')

class BlueColor {
    def is(color) {
        color == 'blue'
    }
}

def blue = new BlueColor()
assert !blue.is('red')
assert blue.is('blue')
assert 'gol'.is("gol")