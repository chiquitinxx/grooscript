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

def closure = { x, y -> x * y}
assert closure.call(5, 3) == 15

assert { x, y -> x * y}.call(3, 5) == 15
assert { x, y -> x * y}(3, 5) == 15

def number = 0
def memoized = { x, y -> number++; x * y }.memoize()
def memoized2 = memoized.memoize()
assert memoized(3, 2) == 6
assert number == 1
assert memoized(3, 2) == 6
assert number == 1
assert memoized2(3, 2) == 6
assert number == 1