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

def (four,five) = [4,5]
assert four == 4
assert five == 5

class Point { float x, y }
Point.metaClass.getAt = { pos ->
    pos == 0 ? x:y
}
def p = new Point(x: 1, y: 2)

def (a, b) = p

assert a == 1
assert b == 2

def (_, month, year) = "18th June 2009".split()
assert "In $month of $year" == "In June of 2009"

def (r, s, t) = [1, 2]
assert r == 1 && s == 2 && t == null

class ItemWithMethod {
    private return12() {
        [1,2]
    }
    def doSomething() {
        def (first, second) = return12()
        [first, second]
    }
}

def (one, two) = new ItemWithMethod().doSomething()
assert one == 1
assert two == 2
