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

def a = 0

def cl1 = { a ++ }
def cl2 = { a += 2 }

def list = [cl1, cl2]

for (Runnable f: list)
    f.run()

assert a == 3

def b = 0

for (Closure cl: list) {
    b++
}

assert b == 2

def c = 0

if (b) c = 1 else c = 2

assert c == 1