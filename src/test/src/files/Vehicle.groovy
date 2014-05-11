package files

/**
 * User: jorgefrancoleza
 * Date: 08/04/14
 */
class Vehicle {
    def engine
    def engineStarted = false

    def start() {
        if (isStarted()) {
            return 'Already started'
        } else {
            engineStarted = true
            return 'Starting...'
        }
    }

    boolean isStarted() {
        engineStarted
    }
}
