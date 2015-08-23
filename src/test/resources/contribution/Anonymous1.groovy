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

def list = [true,false]

assert list[0]
assert !list[1]
// tag::mark[]
def res

switch ([true,false]) {
    case {it[0]}: res = 0; break
    case {it[1]}: res = 1; break
    default : res = 2
}

assert res == 0
// end::mark[]

def f = "fizz"
def b = "buzz"
def count1 = 0
def count2 = 0
(1..100).each{ x ->
    count1++
    count2++
    switch ([count1 == 3, count2 == 5]) {
        case{it[0]&&it[1]} : str= f+b;count1=0;
            count2=0; break
        case{it[0]} : str=f;count1=0; break
        case{it[1]} : str=b;count2=0; break
        default : str=x
    }
    println str
}
