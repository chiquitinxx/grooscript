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

def list = [1,3,7,4,9,2,5]
//False don't modify original list
assert list.sort(false) == [1,2,3,4,5,7,9]

assert list == [1,3,7,4,9,2,5]

assert list.sort() == [1,2,3,4,5,7,9]
assert list == [1,2,3,4,5,7,9]

assert ["hi","hey","hello"] == ["hello","hi","hey"].sort { it.size() }

assert ["hi","hey","hello"] == ["hello","hi","hey"].sort { a, b -> a.size() <=> b.size() }

def orig = ["hello","hi","Hey"]

def sorted = orig.sort(false) { it.toUpperCase() }

assert orig == ["hello","hi","Hey"]

assert sorted == ["hello","Hey","hi"]