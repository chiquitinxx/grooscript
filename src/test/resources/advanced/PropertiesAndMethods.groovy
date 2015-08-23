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

class MyObject {
    def a
    static b
    def method1() {
        println 'method1'
    }

    static prod(a,b) {
        return a*b
    }
}

def myObject = new MyObject()

println 'Properties->'+myObject.properties
//Return at least 2
assert myObject.properties.size() >= 2
assert myObject.properties.b == null
assert myObject.properties.a == null
assert myObject.properties['class'].name == 'advanced.MyObject'

myObject.metaClass.methods.each { MetaMethod method->
    println 'name method->'+method.name
}
//We gonna return at least 2 in javascript
assert myObject.metaClass.methods.size() >= 2

myObject.invokeMethod('method1',null)
assert myObject.invokeMethod('prod',[5,6]) == 30
