
/**
 * User: jorgefrancoleza
 * Date: 09/07/13
 */

interface Car {
    public Car start()
    public void stop()
    public Car move()
    public int getSpeed()
}

class MyCar implements Car {

    int speed = 0
    boolean started = false

    public Car start() {
        started = true
        this
    }

    public void stop() {
        started = false
        speed = 0
    }

    public Car move() {
        if (started) {
            speed++
        }
        this
    }

    public int getSpeed() {
        speed
    }
}

assert new MyCar().start().move().speed == 1