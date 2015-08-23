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
package doc

// tag::truth[]
def a = true
def b = true
def c = false
assert a
assert a && b
assert a || c
assert !c

def numbers = [1,2,3]
assert numbers
numbers = []
assert !numbers

assert ['one':1]
assert ![:]

assert ('Hello World' =~ /World/)

assert 'This is true'
assert !''

def s = ''
assert !("$s")
s = 'x'
assert ("$s")

assert !0
assert 1

assert new Object()
assert !null
// end::truth[]
