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

class Func {
    static int mult(a,b) {
        a * b
    }
}

class MyOne {
    def execute(Closure closure) {
        closure.delegate = this
        closure()
    }

    def executeInTwo(Closure closure) {
        closure.delegate = this
        new MyTwo().execute closure
    }

    def executeWithDelegate(delegate, Closure closure) {
        closure.delegate = delegate
        closure()
    }

    int add(one,two) {
        one + two
    }
}

class MyTwo {
    def execute(Closure closure) {
        closure()
    }
}

def a = 3
def b = 5
def item = new MyOne()
def result = item.execute {
    def res = add(a,b)
    return res
}

assert result == a + b

result = item.executeWithDelegate(Func) {
    mult(a,b)
}

assert result == a * b

def minusFunc = { x,y ->
    x - y
}

result = item.executeWithDelegate(this) {
    minusFunc(a,b)
}

assert result == a - b

assert a + b == item.executeInTwo { add(a,b) }
