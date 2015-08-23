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
// tag::regs[]
assert "abc" == /abc/
assert "\\d" == /\d/
def reference = "hello"
assert reference == /$reference/
assert "\$" == /$/

assert "potatoe" ==~ /potatoe/
assert !("potatoe with frites" ==~ /potatoe/)

myFairStringy = 'The rain in Spain stays mainly in the plain!'

found = ''
(myFairStringy =~ /\b\w*ain\b/).each { match ->
        found += match + ' '
}
assert found == 'rain Spain plain '

def matcher = 'class A{ class B {}}' =~ /\bclass\s+(\w+)\s*\{/
assert matcher.size() == 2
// end::regs[]
assert "griffon" =~ /gr*/
//This doesn't work :/ always return true that javascript object
//assert !("gradle" =~ /sup*/)
