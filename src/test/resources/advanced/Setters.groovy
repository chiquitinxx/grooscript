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

class A {
    def value
}

def a = new A()

a.value=5
assert a.value==5

a.setValue(6)
assert a.value == 6

def number = 0

a.metaClass.setProperty = { name,value ->
    number++
}

a.value = 7
assert number == 1
assert a.value == 6

a.metaClass.setProperty = { name,value ->
    a.@"$name" = value
}

a.value = 8
assert number == 1
assert a.value == 8

class B {
    def b
    def number

    B(value) {
        number = value
    }

    def setB(value) {
        b = value
        this.number = 5*value
    }

    def putValueToB(value) {
        b = value
    }
}

b = new B(5)
b.b = 5
assert b.b == 5
assert b.number == 25

b.setB(6)
assert b.b == 6
assert b.number == 30

b.putValueToB(9)
assert b.b == 9
assert b.number == 30
