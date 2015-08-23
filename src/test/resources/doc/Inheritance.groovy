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
package doc

// tag::interface[]
interface Move {
    def start()
}
// end::interface[]

// tag::inheritance[]
class Bicycle implements Move {
    int speed

    Bicycle() {
        speed = 0
    }

    def start() {
        speed = 1
    }
}

class MountainBike extends Bicycle {
    def seatHeight

    MountainBike() {
        super() // <1>
        seatHeight = 5
    }

    // <2>
    //def start() {
    //    super.start()
    //}
}

class CoolMountainBike extends MountainBike {
    CoolMountainBike() {
        super()
    }
}
// end::inheritance[]

def bicycle = new Bicycle()
def mountainBike = new MountainBike()
def coolMountainBike = new CoolMountainBike()

assert bicycle.speed == 0
assert mountainBike.speed == 0
assert mountainBike.seatHeight == 5
assert coolMountainBike.speed == 0
assert coolMountainBike.seatHeight == 5

mountainBike.start()
assert mountainBike.speed == 1

coolMountainBike.start()
assert coolMountainBike.speed == 1