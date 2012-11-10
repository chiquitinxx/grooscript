package advanced

/**
 * JFL 08/11/12
 */

class Robot {

    def data = [:]
    def getPosition() {}
    def getMaxPosition() {}
    def getTemperature() {}
    def getRadar() { [:] }
    def move(dir) {}
    def canShoot(x,y) {}
    def shoot(x,y) {}
    def distance(p1,p2) {}

    def actions() {
        //Example robot
        if (data.direction) {
            if (getPosition().x<5) {
                data.direction = 1
            }
            if (getPosition().x>(getMaxPosition().x-5)) {
                data.direction = 2
            }
            move data.direction
        } else {
            data.direction = 1
        }
        move data.direction
        if (getTemperature()<70) {
            def radar = getRadar()
            def maxDistance = 999
            if (radar.size()>0) {
                radar.each { name,position ->
                    def distan = distance getPosition(),position
                    if (canShoot(position.x,position.y) && distan < maxDistance) {
                        maxDistance = distan
                        //Only will do last shoot
                        shoot position.x,position.y
                    }
                }
            }
        }
    }
}

def robot = new Robot()
robot.actions()
