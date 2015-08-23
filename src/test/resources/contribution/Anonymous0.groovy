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

def rockstar
def defaultrockstar = rockstar ?: "Elvis Presley"
assert defaultrockstar == "Elvis Presley"

def other = 'Queen'
other = other ? 'Queen 2' : 'Queen 3'
assert other == 'Queen 2'

assert '1'*0 == ''
assert '1'*0.9 == ''
assert !('1'*0)
//println '1'*2
assert '1'*2 == '11'


100.times{it->
    //println"->${(it%3/2)}"
    println'Fizz'*((it%3)/2)+'Buzz'*((it%5)/4)?:++it
}

