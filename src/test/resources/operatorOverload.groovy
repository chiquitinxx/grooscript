/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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