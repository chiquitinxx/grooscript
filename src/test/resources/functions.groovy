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
numberFive = { 5 }
assert numberFive() == 5

multiplyBy2 = { it * 2 }
multiplyBy3 = { it * 3 }

def data = 1
while (data<numberFive()) {
    data = multiplyBy2(data)
}

assert data == 8

doXtimes = { x,cl ->
    x.times {
        cl(it)
    }
}

def text = ''
doXtimes(3) { it ->
    text += 'a'+it
}

assert text == 'a0a1a2'

class Guard {
    def value
    def closures = []
    Guard (initial) {
        value = initial
    }

    def apply() {
        closures.each {
            value = it(value)
        }
    }
}
def guard = new Guard(1)
guard.closures << multiplyBy2
guard.closures << multiplyBy3
guard.apply()

assert guard.value==6