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

import org.grooscript.asts.GsNotConvert

def map = [one:1,two:2]
map.remove('one')

assert map.size() == 1

class Mf1 {
    def value = 0
    @GsNotConvert
    def two() {
        value = 2
    }
    def twoTwo() {
        two() * two()
    }
}
//In the test we add code to js file as if two() exist
assert new Mf1().twoTwo() == 4

class Mf2 {
    def one = 1
    def two = 2
    def list = ['one','two']

    def giveMe(name) {
        def result
        def item = name
        list.each { it ->
            if (!result) {
                result = this."${item}"
            }
        }
        return result
    }
}

assert new Mf2().giveMe('two') == 2
