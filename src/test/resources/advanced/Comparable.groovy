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

assert 1 == 1.0
assert 1.1 > 1
assert "a" > "A"
assert "B" > "A"
assert "hello" > "Hello"
assert 6
def date = new Date()
assert date + 1 > date

def oneDate = new Date(87398475634)
assert oneDate.format('dd-MM-yyyy') == '08-10-1972'
// In CI server HH in format is distinct. I suppose hour depends of machine
assert oneDate.format('dd-MM-yy mm:ss') == '08-10-72 21:15'
assert oneDate.time == 87398475634