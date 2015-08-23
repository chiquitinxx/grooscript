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

class Const {
    static final FIVE = 5
    static doubleMe = { value ->
        value * 2
    }
}

import static contribution.Const.*

def value = 1

contribution.Const.FIVE.times {
    value = contribution.Const.doubleMe value
}

assert doubleMe(FIVE) == 10

assert value == 32

assert (1..FIVE).collect { it * 2 }.sum() == 30
