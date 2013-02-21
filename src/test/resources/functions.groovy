
/**
 * User: jorgefrancoleza
 * Date: 20/02/13
 */

numberFive = { 5 }
assert numberFive() == 5

multiplyBy2 = { it * 2 }
multiplyBy3 = { it * 3 }

def data = 1
while (data<numberFive()) {
    data = multiplyBy2(data)
}

assert data == 8

doXtimes = { x,cl ->
    x.times {
        cl(it)
    }
}

def text = ''
doXtimes(3) { it ->
    text += 'a'+it
}

assert text == 'a0a1a2'

class Guard {
    def value
    def closures = []
    Guard (initial) {
        value = initial
    }

    def apply() {
        closures.each {
            value = it(value)
        }
    }
}
def guard = new Guard(1)
guard.closures << multiplyBy2
guard.closures << multiplyBy3
guard.apply()

assert guard.value==6