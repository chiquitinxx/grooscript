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

trait NewNamed {
    String name
    int number = 5
    void setName(newName) {
        println 'new name:'+newName
        name = newName
    }
    int getNumber() {
        println 'Getting number with: '+number
        number
    }
}
class NewPerson implements NewNamed {}
def p = new NewPerson(name: 'Bob')
assert p.name == 'Bob'
assert p.getName() == 'Bob'

def p2 = new NewPerson(name: 'Foo')
assert p2.name == 'Foo'
assert p.name == 'Bob'
assert p.number == 5
p.number = 7
assert p.number == 7
assert p2.number == 5
