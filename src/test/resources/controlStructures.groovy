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
def a = 0
if (true) a = 1 else a = -1
assert a == 1
(a > 0) ? (a = 3) : (a = 0)
assert a == 3

def name = 'Groovy'
a = 0

switch (name) {
    case 'JavaScript': a = -1; break
    case 'GROOVY': a = 1; a = 2;break
    case 'Groovy':a = 3; break
    default : a = 4
}

assert a == 3

a=0

while (a<5) {
    a = a + 2
    a--
}
assert a == 5

def store = ''
for (i in [1, 2, 3]) {
    store += i
}
assert store == '123'