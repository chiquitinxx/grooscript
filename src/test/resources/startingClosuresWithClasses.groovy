/**
 * JFL 02/09/12
 */
sayHelloTo = { it ->
    return "Hello ${it}!"
}

assert sayHelloTo('Groovy') == 'Hello Groovy!'

def getCode = { house ->
    def minValue = 5
    if (house?.number > minValue) {
        return 1
    } else {
        return 0
    }
}

class House {
    def address
    def number
    def code

    def applyCode(closure) {
        code = closure(this)
    }

}

def house = new House([address:'Groovy Street', number:3])
assert  house.number == 3
assert !house.code
assert house.code == null

house.applyCode(getCode)
assert house.code == 0

def salute = ''
5.times {it-> salute+=it}
assert salute == "01234"
