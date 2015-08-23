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

def customSum = { ... elements ->
    elements.inject(0) { val, acc ->
        acc + val
    }
}

assert customSum() == 0
assert customSum(5) == 5
assert customSum(1, 2, 3, 4, 5) == 15
assert customSum(1, 2, 3, 4, 5) == customSum(10, 5)
def list = [1, 2, 3, 4, 5] as Object[]
assert customSum(list) == 15