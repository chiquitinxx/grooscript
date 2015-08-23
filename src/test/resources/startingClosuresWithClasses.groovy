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
sayHelloTo = { it ->
    return "Hello ${it}!"
}

assert sayHelloTo('Groovy') == 'Hello Groovy!'

def getCode = { house ->
    def minValue = 5
    if (house?.number > minValue) {
        return 1
    } else {
        return 0
    }
}

class House {
    def address
    def number
    def code

    def applyCode(closure) {
        code = closure(this)
    }

}

def house = new House([address:'Groovy Street', number:3])
assert  house.number == 3
assert !house.code
assert house.code == null

house.applyCode(getCode)
assert house.code == 0

def salute = ''
5.times {it-> salute+=it}
assert salute == "01234"
