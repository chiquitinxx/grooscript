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

int color(String name) {
    if (name == 'red')
        1
    else if (name == 'green')
        2
    else
        -1
}

assert color('red') == 1
assert color('green') == 2
assert color('yellow') == -1

boolean isInt(String s) {
    try {
        Integer.parseInt(s)
        true
    } catch (NumberFormatException e) {
        false
    } finally {
        println 'done'
    }
}

assert isInt('123')
assert !isInt('abc')