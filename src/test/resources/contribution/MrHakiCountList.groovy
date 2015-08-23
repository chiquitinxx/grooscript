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

def list = [0, 1, 2, 1, 1, 3, 2, 0, 1]

assert 9 == list.size()
assert 2 == list.count(0)
assert 4 == list.count(1)
assert 2 == list.count(2)
assert 1 == list.count(3)

def listItems = ['Groovy', 'Grails', 'Java']
assert listItems.count { it.startsWith('G') } == 2

def numbers = [1, 2, 3, 4]
assert numbers.count { it > 2 } == 2