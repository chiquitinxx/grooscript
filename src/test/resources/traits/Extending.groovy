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

trait WithName {
    String traitName = 'WithName'
    String name
    int count = 0
    String getName() {
        count = count + 1
        name
    }
}
trait Friendly extends WithName {
    String traitName = 'Friendly'
    String introduce() { "Hello, I am $name" }
}
class Guy implements Friendly {
    Guy(newName) {
        name = newName
        traitName = 'New' + traitName
    }
    def hello() {
        traitName
    }
}
def g = new Guy('Groovy')
assert g.introduce() == 'Hello, I am Groovy'
assert g instanceof Friendly, 'Instance Friendly'
assert g instanceof Guy, 'Instance Guy'
assert g instanceof WithName, 'Instance WithName'
assert g.traitName == 'NewFriendly'
assert g.count == 1, 'Count'
assert g.hello() == 'NewFriendly'
