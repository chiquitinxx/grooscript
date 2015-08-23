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
class Base {
    def prop
    def method() {}
}
assert new Base()

class MyClass {
    def name
    def numberMessages = 0

    MyClass(startingName) {
        name = startingName
    }

    MyClass() {
        name = ''
    }

    def saySomething(message) {
        println "${name}: ${message}"
        numberMessages++
    }

    def tellMeNumber() {
        def name = 'Name'
        def numberMessages = -5
        assert name == 'Name'
        assert numberMessages == -5
        return this.numberMessages
    }
}

myClass = new MyClass()

assert myClass.numberMessages == 0
assert !myClass.name
assert myClass.name == ''

myClass.name = 'Fan'
myClass.saySomething('GroovyRocks')

assert myClass.numberMessages == 1
assert myClass.name == 'Fan'
assert myClass.tellMeNumber() == 1

def myClass2 = new MyClass('George')
assert myClass2.name == 'George'