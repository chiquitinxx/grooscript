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
package classes

class MySlimCar {
    def inAction = false

    def run() {
        inAction = true
    }

    def stop() {
        inAction = false
    }
}

@Category(MySlimCar)
class BondCar {
    def shootMissile() {
        'PWN!'
    }

    def jump() {
        if (inAction) {
            return 'FIUUUUU'
        } else {
            return 'NONONO'
        }
    }
}

def myCar = new MySlimCar()
use(BondCar) {
    assert myCar.jump() == 'NONONO'
    assert myCar.shootMissile() == 'PWN!'
    myCar.run()
    assert myCar.inAction
    assert myCar.jump() == 'FIUUUUU'
}

@Category(LinkedHashMap)
class SuperSize {
    def superSize() {
        this.size() * this.size()
    }
}

use(SuperSize) {
    assert [one: 1, two: 2].superSize() == 4
}

@Category(ArrayList)
class SuperSizeList {
    def superSize() {
        this.size() * this.size()
    }
}

use(SuperSizeList) {
    assert [1, 2, 3].superSize() == 9
}

@Category(Number)
class Calculates {
    def triple() {
        this * 3
    }
}

use(Calculates) {
    assert 5.triple() == 15
}
