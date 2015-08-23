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

def a = [1,2,3,4,5]*.multiply(2)
assert a*.power(2) == [4, 16, 36, 64, 100]
assert a == [2, 4, 6, 8, 10]

def list = [12, 20, 34]
def result = list.collectMany { [it, it*2, it*3] }

assert result == [12,24,36,20,40,60,34,68,102]
assert list == [12, 20, 34]

list = [1,2,3,5,7,2,9]

assert [1,2,3] == list.takeWhile { it < 5 }
assert [5,7,2,9] == list.dropWhile { it < 5 }

def reverse = []
list.reverseEach {
    reverse << it
}
assert reverse == [9,2,7,5,3,2,1]
list.remove(2)
assert list ==  [1,2,5,7,2,9]
list.removeAll { it < 5}
assert list ==  [5,7,9]
list.addAll(2,[2,4])
assert [5,7,2,4,9] == list
assert true == list.addAll([11])


list = [ 1, 2, 3, 4, 5, 6, 7 ]
def coll = list.collate( 3 )
assert coll == [ [ 1, 2, 3 ], [ 4, 5, 6 ], [ 7 ] ]

list = [ 1, 2, 3, 4 ]
coll = list.collate( 3, 1 )
assert coll == [ [ 1, 2, 3 ], [ 2, 3, 4 ], [ 3, 4 ], [ 4 ] ]

def strings = [ 'a', 'b', 'c' ]
assert strings.drop( 0 ) == [ 'a', 'b', 'c' ]
assert strings.drop( 2 ) == [ 'c' ]
assert strings.drop( 5 ) == []
