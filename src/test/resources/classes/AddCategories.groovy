package classes

/**
 * User: jorgefrancoleza
 * Date: 02/12/13
 */

class MySlimCar {
    def inAction = false

    def run() {
        inAction = true
    }

    def stop() {
        inAction = false
    }
}

@Category(MySlimCar)
class BondCar {
    def shootMissile() {
        'PWN!'
    }

    def jump() {
        if (inAction) {
            return 'FIUUUUU'
        } else {
            return 'NONONO'
        }
    }
}

def myCar = new MySlimCar()
use(BondCar) {
    assert myCar.jump() == 'NONONO'
    assert myCar.shootMissile() == 'PWN!'
    myCar.run()
    assert myCar.inAction
    assert myCar.jump() == 'FIUUUUU'
}

@Category(LinkedHashMap)
class SuperSize {
    def superSize() {
        this.size() * this.size()
    }
}

use(SuperSize) {
    assert [one: 1, two: 2].superSize() == 4
}

@Category(ArrayList)
class SuperSizeList {
    def superSize() {
        this.size() * this.size()
    }
}

use(SuperSizeList) {
    assert [1, 2, 3].superSize() == 9
}

@Category(Number)
class Calculates {
    def triple() {
        this * 3
    }
}

use(Calculates) {
    assert 5.triple() == 15
}
