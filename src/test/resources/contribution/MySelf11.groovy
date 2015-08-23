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
package contribution

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