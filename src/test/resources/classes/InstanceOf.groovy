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
package classes

class A {

}

class B extends A {

}

b = new B()

assert new A() instanceof A
assert b instanceof B
assert b instanceof A

assert "1" instanceof String
assert !("1" instanceof Number)
assert 2 instanceof Number
assert 3.4 instanceof Number
