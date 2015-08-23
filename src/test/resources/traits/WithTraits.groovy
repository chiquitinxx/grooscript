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

trait A { String methodFromA() { 'A' } }
trait B { String methodFromB() { 'B' } }

class C {}
def c = new C()

def d = c.withTraits A, B
assert d.methodFromA() == 'A'
assert d.methodFromB() == 'B'

def e = new C().withTraits A
assert e.methodFromA() == 'A'
try {
    e.methodFromB()
    assert false, 'Must fail!'
} catch (ex) {
    assert true
}