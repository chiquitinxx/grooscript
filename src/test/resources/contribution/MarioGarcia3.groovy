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

def a = [a:1]
def b = [b:2, c:3]

assert a << b == [a:1, b:2, c:3]
assert a + b == [a:1, b:2, c:3]
def map = a << b
assert map.size() == 3, "map size is ${map.size()}"
def map2 = a + b
assert map2.size() == 3, "map2 size is ${map2.size()}"
a.putAll(b)
assert a.size() == 3, "a size is ${a.size()}"

def one = [start: { println 'start'}]
def two = [second: { println 'second'}]
def three = [third: { println 'third'}, four: {println 'four'}]
def all = one << two << three

assert all.size() == 4, "all size is ${all.size()}"
all.each { it.value() }
