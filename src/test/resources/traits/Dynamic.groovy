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

trait SpeakingDuck {
    String speak() { quack() }
}
class CoolDuck implements SpeakingDuck {
    String methodMissing(String name, args) {
        "${name.capitalize()}!"
    }
}
def d = new CoolDuck()
assert d.speak() == 'Quack!'

trait DynamicObject {
    private Map props = [:]
    def methodMissing(String name, args) {
        name.toUpperCase()
    }
    def propertyMissing(String prop) {
        props['prop']
    }
    void setProperty(String prop, Object value) {
        props['prop'] = value
    }
}

class MyDynamic implements DynamicObject {
    String existingProperty = 'ok'
    String existingMethod() { 'ok' }
}
def md = new MyDynamic()
assert md.existingProperty == 'ok'
assert md.foo == null
md.foo = 'bar'
assert md.foo == 'bar'
assert md.existingMethod() == 'ok'
assert md.someMethod() == 'SOMEMETHOD'
