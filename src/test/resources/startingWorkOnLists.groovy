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
//Some ranges
def range = 0..8
assert !range.contains(10)
assert (0..10).contains(5)

def log = ''
for (i=0;i<2;i++) {
    log += i;
}
assert log == '01'
log = ''
for (element in 5..9){
    log += element
}
assert log == '56789'
log = ''
for (element in 9..5){
    log += element
}
assert log == '98765'
log = ''
(9..<5).each { element ->
    log += element
}
assert log == '9876'

//Lists
def list = [5, 6, 7, 8]
assert list.get(2) == 7
assert list[2] == 7

def emptyList = []
assert emptyList.size() == 0
emptyList.add(5)
assert emptyList.size() == 1

assert list[1..2] == [6,7]

longList = (0..1000).toList()
assert longList[555] == 555

myList = []
myList <<  'a' << 'b'
assert myList.size() == 2
assert myList[1] == 'b'
