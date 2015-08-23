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

def cl1 = { a ->
    a
}

def cl2 = { a ->
    a > 0 ? a : 0
}

def cl3 = { a ->
    if (a > 0) {
        a
    } else {
        0
    }
}

def cl4 = { a ->
    if (a > 0) a
    else 0
}

def cl5 = { a ->
    if (a > 0) {
        if (a > 5) a
        else 5
    } else {
        0
    }
}

def cl6 = { a ->
    if (a > 0) {
        if (a > 3) {
            3
            4
        } else {
            5
        }
        return 1
    } else {
        0
    }
}

assert cl1(1) == 1
assert cl2(1) == 1
assert cl2(-1) == 0
assert cl3(1) == 1
assert cl3(-1) == 0
assert cl4(1) == 1
assert cl4(-1) == 0
assert cl5(1) == 5
assert cl5(9) == 9
assert cl5(-1) == 0
assert cl6(5) == 1
assert cl6(-1) == 0
