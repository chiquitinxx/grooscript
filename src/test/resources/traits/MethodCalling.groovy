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

trait Methods {
    static one() {
        1
    }
    static ONE = 1
    static two(other) {
        ONE = ONE + 1
        [ONE, one(), other]
    }
    def three() {
        3
    }
    def five = 5
    def four() {
        [three(), one(), five]
    }
}

class WithMethods implements Methods {
    def init() {
        two(4) + four()
    }
}

assert WithMethods.one() == 1
assert WithMethods.two(2) == [2, 1, 2]
def withMethods = new WithMethods()
assert withMethods.init() == [3, 1, 4, 3, 1, 5]
