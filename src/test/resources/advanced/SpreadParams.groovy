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

def list = [1, 2, 3, 4]

def sum = { ... values ->
    values.inject(0) { val, acc ->
        acc = acc + val
    }
}

assert sum(*list) == 10
assert sum(*list, *list) == 20
assert sum(*list, 5, *list) == 25
assert sum(12, *list) == 22
assert sum(*list, 13) == 23
