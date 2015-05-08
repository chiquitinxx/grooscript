package files

/**
 * User: jorgefrancoleza
 * Date: 08/04/14
 */
class Car extends Vehicle {
    def company
    def garage

    def carStarted() {
        isStarted()
    }

    Garage garage() {
        if (!garage) {
            garage = new Garage()
        }
        garage
    }
}
