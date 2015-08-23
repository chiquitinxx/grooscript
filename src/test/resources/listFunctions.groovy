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
def list = [1,2]
assert list.reverse()[0] == 2
list << 3
assert list.size() == 3
assert list.sort()[0] == 1

list = [1,'A',3]
list.remove(2)
list.remove('A')
assert list.size() == 1
assert list[0] == 1

list = ['a','b','c','b','d','c']
list.removeAll(['b','c'])
assert list.size() == 2
assert list[1] == 'd'

def doubled = [1,2,3].collect{ item ->
    item*2
}
assert doubled == [2,4,6]
assert doubled[0] == 2
assert doubled[1] == 4
assert doubled[2] == 6
assert doubled.size() == 3

// tag::lists[]
def odd = [1,2,3].findAll{ item ->
    item % 2 == 1
}
assert odd.size() == 2
assert odd[0] == 1
assert odd[1] == 3

assert [1,2] + [3,5] == [1,2,3,5]
assert [1,2,3,4,5] - [3,5] == [1,2,4]

list = [1, 2, 2, 3, 4]
assert list.unique() == [1, 2, 3, 4]
list = [1, 2, 2, 3, 4]
def uniqList = list.unique(false)
assert list == [1, 2, 2, 3, 4]
assert uniqList == [1, 2, 3, 4]
// end::lists[]
