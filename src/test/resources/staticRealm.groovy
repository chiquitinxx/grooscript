
/**
 * JFL 06/11/12
 */

class Medusa {

    static number = 0
    public static thing = ''
    def frozen = false
    private static final DATA = 'data'

    static tellMeNumber() {
        return number
    }

    Medusa() {
        number++
    }

    def isFrozen() {
        def number = tellMeNumber()
        frozen
    }

    def getData() {
        DATA
    }
}

def med1 = new Medusa()
def med2 = new Medusa()

assert Medusa.number == 2
assert Medusa.tellMeNumber() == 2

assert med1.number == 2
assert med1.tellMeNumber() == 2
assert med2.number == 2
med1.number = 3

assert med2.number == 3
assert med2.tellMeNumber() == 3

med2.frozen = true
assert med2.isFrozen()
assert !med1.isFrozen()

assert med1.data == 'data'
