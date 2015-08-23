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

// tag::class_info[]
class A { def a }
class B extends A {}
def a = new A()
def b = new B()

assert a.class == a.getClass()
assert a.metaClass == a.getMetaClass()
assert a.getClass().getName() == 'contribution.A'

assert a.getClass().getSimpleName() == 'A'
assert b instanceof A
assert b instanceof contribution.B
assert Class.forName('contribution.B') == B
assert A.newInstance()
// end::class_info[]
assert a.class.name == a.class.getName()
assert a.class.simpleName == a.class.getSimpleName()
assert new B() instanceof A
assert b instanceof B
assert b instanceof contribution.B
assert A.newInstance()
assert Class.forName('contribution.A') == A

//NOT SUPPORTED
//assert b.class.superclass == A
//assert b.class.getSuperclass() == A
//assert a.class.newInstance()
//assert a.getClass().newInstance()
