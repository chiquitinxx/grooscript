package advanced
/**
 * User: jfl
 * Date: 07/01/13
 */

assert 1 == 1.0
assert 1.1 > 1
assert "a" > "A"
assert "B" > "A"
assert "hello" > "Hello"
assert 6
def date = new Date()
assert date + 1 > date

def oneDate = new Date(87398475634)
assert oneDate.format('dd-MM-yyyy') == '08-10-1972'
assert oneDate.format('dd-MM-yy HH:mm:ss') == '08-10-72 14:21:15'
assert oneDate.time == 87398475634

/* This doesn't work in groovy as expected, have to implements java.util.Comparable to work
class Car {
    def power

    def int compareTo(Object o) {
        if (o && o instanceof Car) {
            return this.power <=> o.power
        } else {
            return 0
        }
    }
}

def car = new Car(power: 100)
def car2 = new Car(power: 200)
assert car2 > car
*/