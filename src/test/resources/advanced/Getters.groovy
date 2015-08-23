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
package advanced

class C {
    def value
}

def c = new C()

c.value=5
assert c.getValue() == 5
assert c.value == 5

def number = 0

c.metaClass.getProperty = { name ->
    number++
    0
}

assert !c.value
assert number == 1

c.metaClass.getProperty = { name ->
    c.@"$name"
}

assert c.getProperty('value') == 5

class D {
    def d
    def times = 0

    def getD() {
        times++
        return d
    }

    def tellMeDValue() {
        d
    }
}

def d = new D()
d.d = 5

assert d.times == 0
assert d.d == 5
assert d.times == 1

assert d.tellMeDValue() == 5
assert d.times == 1
