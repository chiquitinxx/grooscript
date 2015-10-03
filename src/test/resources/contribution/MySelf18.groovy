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

class This1 {
    def value

    def execute(closure) {
        closure(this.value)
    }
}

class This2 {
    def value
    def otherThis

    def execute(closure) {
        2.times {
            closure(otherThis.value)
            closure(this.value)
            closure(otherThis.execute({ it * this.value}))
        }
    }
}

def this1 = new This1(value: 5)
assert this1.execute({ it * 2}) == 10

def sum = 0
def this2 = new This2(value: 3, otherThis: this1)
this2.execute({ sum += it })
assert sum == 46