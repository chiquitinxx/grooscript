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

class Funcs {

    def value = 2
    static valueStatic = 5

    public five() {
        return 5
    }

    def giveFive() {
        return this.&five
    }

    def plus(a=value,b=valueStatic) {
        a+b
    }

    def giveMePlus() {
        return this.&plus
    }
}

def funcs = new Funcs()
def giveMeFive = funcs.&five

assert giveMeFive() == 5
assert funcs.giveFive()() == 5
assert funcs.giveFive().call() == 5

assert funcs.giveMePlus()(6,3) == 9
funcs.value = 7
assert funcs.giveMePlus()() == 12