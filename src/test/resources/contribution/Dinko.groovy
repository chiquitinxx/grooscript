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

def a = 5

assert a == 5
assert 3 == ( a = 3 )
assert a == 3

trait TraitA {
    def foo
    def getBar() { foo = 42 }
}

class ClassC implements TraitA {}

def classc = new ClassC()
assert classc.bar == 42 // In groovy returns null, a bug in groovy 2.3.7
assert classc.foo == 42
