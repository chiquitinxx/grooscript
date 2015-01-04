package contribution

/**
 * Created by jorge on 15/11/14.
 */
trait Color {
    static COLORS = ['black', 'white']

    String randomColor() {
        COLORS[new Random().nextInt(COLORS.size())]
    }
}

class Car implements Color {

    def color
    static number
    def data

    Car() {
        color = randomColor()
        number = 1
        data = 2
    }

    def getAllData() {
        [number, data]
    }
}

def car = new Car()
assert car.color
assert car.data == 2
assert Car.number == 1
assert car.allData == [1, 2]