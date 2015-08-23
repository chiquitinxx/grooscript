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
package traits

trait Equal {

    int number = 5

    boolean equalProperties() {
        def props = this.propertyList()
        if (props.size() == 2) {
            equalsTwo(*props)
        } else if (props.size() == 3) {
            this.equalsThree(*props)
        } else {
            throw new Exception()
        }
    }

    boolean allNumberFive() {
        propertyList().every {
            it == this.number && it == this.getNumber() && it == number
        }
    }

    private boolean equalsTwo(a, b) {
        a == b && b == a
    }

    private boolean equalsThree(a, b, c) {
        this.equalsTwo(a, b) && this.equalsTwo(b, c) && equalsTwo(c, a)
    }
}

class MyTwoNumbers implements Equal {
    def a = 5
    def b = 5
    def propertyList() {
        [a, b]
    }
}

class MyThreeNumbers implements Equal {
    def a = 6
    def b = 6
    def c = 6

    def propertyList() {
        [a, b, c]
    }
}

def myTwoNumbers = new MyTwoNumbers()
assert myTwoNumbers.equalProperties()
assert myTwoNumbers.allNumberFive()
myTwoNumbers.a = 7
assert !myTwoNumbers.equalProperties()
assert !myTwoNumbers.allNumberFive()

def myThreeNumbers = new MyThreeNumbers()
assert myThreeNumbers.equalProperties()
myThreeNumbers.c = 8
assert !myThreeNumbers.equalProperties()
