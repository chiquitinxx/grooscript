package doc

// tag::interface[]
interface Move {
    def start()
}
// end::interface[]

// tag::inheritance[]
class Bicycle implements Move {
    int speed

    Bicycle() {
        speed = 0
    }

    def start() {
        speed = 1
    }
}

class MountainBike extends Bicycle {
    def seatHeight

    MountainBike() {
        super() // <1>
        seatHeight = 5
    }

    // <2>
    //def start() {
    //    super.start()
    //}
}

class CoolMountainBike extends MountainBike {
    CoolMountainBike() {
        super()
    }
}
// end::inheritance[]

def bicycle = new Bicycle()
def mountainBike = new MountainBike()
def coolMountainBike = new CoolMountainBike()

assert bicycle.speed == 0
assert mountainBike.speed == 0
assert mountainBike.seatHeight == 5
assert coolMountainBike.speed == 0
assert coolMountainBike.seatHeight == 5

mountainBike.start()
assert mountainBike.speed == 1

coolMountainBike.start()
assert coolMountainBike.speed == 1