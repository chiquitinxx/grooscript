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

trait TraitWithProps {
    static one
    def two
}

class MyObjectWithTrait implements TraitWithProps {
    def three
    static four
}

def object = new MyObjectWithTrait()
assert object.properties.size() == 5
assert object.properties.findAll { it.key == 'pepe' } == [:]
assert object.properties.findAll { it.key == 'class' }.collect { it.key } == ['class']
assert object.properties.findAll { it.key == 'one' }.collect { it.key } == ['one']
assert object.properties.findAll { it.key == 'two' }.collect { it.key } == ['two']
assert object.properties.findAll { it.key == 'three' }.collect { it.key } == ['three']
assert object.properties.findAll { it.key == 'four' }.collect { it.key } == ['four']
