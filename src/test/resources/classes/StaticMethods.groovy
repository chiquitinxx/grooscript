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
package classes

class WithStaticMethods {
    static list = ['me', 'groovy']
    static closures = ['one': {
        go 'goOne'
    }]

    static sayHello() {
        def result = []
        list.each {
            result << clone(it)
        }
    }

    static clone(item) {
        item
    }
}

def list = []
WithStaticMethods.sayHello().each {
    list << it.toUpperCase()
}

assert list == ['ME', 'GROOVY']

class Go {
    def go(value) {
        value
    }

    def processClosure(cl) {
        cl.delegate = this
        cl()
    }
}

assert new Go().processClosure(WithStaticMethods.closures['one']) == 'goOne'

class MyGo {

    def map = [a: 3]
    def two() {
        2
    }

    static start() {
        MyGo map = new MyGo()
        [launch: { ->
            map.map.a * map.two()
        }]
    }
}

assert MyGo.start().launch() == 6