
/**
 * Created by jorge on 04/12/14.
 */
class OperateRobot {
    def plus(OperateRobot robot) {
        5
    }
    def minus(OperateRobot robot) {
        4
    }
    def multiply(OperateRobot robot) {
        3
    }
    def div(OperateRobot robot) {
        2
    }
    def power(OperateRobot robot) {
        1
    }
    def mod(OperateRobot robot) {
        6
    }
    def leftShift(OperateRobot robot) {
        7
    }
    def rightShift(OperateRobot robot) {
        8
    }
}

def robotA = new OperateRobot()
def robotB = new OperateRobot()
assert robotA + robotB == 5
assert robotA - robotB == 4
assert robotA * robotB == 3
assert robotA / robotB == 2
assert robotA % robotB == 6
assert robotA << robotB == 7
assert robotA >> robotB == 8