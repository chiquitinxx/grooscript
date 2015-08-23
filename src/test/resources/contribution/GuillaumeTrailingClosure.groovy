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

// tag::mark[]
void unless(boolean b, Closure c) {
    if (!b) c()
}

def (speed, limit) = [80, 90]
def tooFast = false

unless (speed > limit) {
    tooFast = true
}

assert tooFast
// end::mark[]
import java.util.ArrayList as L

def list = new L()
assert list == []

import static java.lang.Integer.parseInt as parse
import static java.lang.Float.parseFloat as parseF

assert 5 == parse('5')
assert 5.45 as float == parseF('5.45')