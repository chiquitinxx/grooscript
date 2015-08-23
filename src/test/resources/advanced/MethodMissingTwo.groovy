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

class MethodMissing2 {

    static number = 0

    def theSize = {
        return it.size()
    }

    def callMethod(String name, args) {
        this."${name}"(args)
    }

    def methodMissing(String name, args) {
        if (name=='giveMeSize') {
            return theSize(args[0])
        } else {
            return "${name}(${args[0]})(${number++})"
        }
    }
}

def name = 'Name'
def params = [0,2.3,'hola']

def mm = new MethodMissing2()

assert mm.callMethod(name,params) == 'Name([0, 2.3, hola])(0)'
assert mm.callMethod(name,null) == 'Name(null)(1)'
assert mm.callMethod('giveMeSize',params) == 3