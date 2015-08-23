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
package doc

// tag::object[]
class MyObject {
    private static final NUMBER = 6
    def name = 'MyObject'
    Map map

    MyObject() {
        map = [:]
    }

    MyObject(name) {
        map = [newName: name]
        this.name = name
    }

    private addToMap(key, value) {
        map[key] = value
    }

    void register(listActions) {
        listActions.each {
            addToMap(it.name, it.data ?: NUMBER)
        }
    }
}
// end::object[]

assert new MyObject().map == [:]
assert new MyObject().name == 'MyObject'
assert new MyObject('coolName').map == [newName: 'coolName']
assert new MyObject('coolName').name == 'coolName'

def myObject = new MyObject()
myObject.register([[name: 'inc', data: 5], [name: 'event']])
assert myObject.map == [inc: 5, event: 6]