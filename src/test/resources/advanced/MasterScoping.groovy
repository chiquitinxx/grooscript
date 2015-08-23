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

class Data {

    def static COUNT = 0

    def a
    def b

    def boolean equals(o) {
        o && o.a == this.a && o.b == this.b
    }
}

class Functions {

    def static number = 0

    def process(items) {
        def result = []
        items.each { item ->
            def data = new Data()
            data.a = number++
            data.b = Data.COUNT++ % 2
            //TODO leftShift
            result << data
        }

        result
    }
}

func = new Functions()

result = func.process([1,2,3,4,5])

assert result && result.size() == 5
assert result[0] == new Data(a: 0, b: 0)
assert result[1] == new Data(a: 1, b: 1)
assert result[2] == new Data(a: 2, b: 0)
