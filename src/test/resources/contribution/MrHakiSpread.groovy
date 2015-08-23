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

class SpreadSimple {
    String speak(Integer n, String text, Date date) {
        def out = ''
        n.times {
            out += "Say $text on ${date.format('yyyy-MM-dd')}.\n"
        }
        out
    }
}

// Spread params list for speak() method.

def params = [
        2,
        "hello world",
        new Date().parse("yyyy/MM/dd", "2009/09/01")
]
assert '''Say hello world on 2009-09-01.
Say hello world on 2009-09-01.
''' == new SpreadSimple().speak(*params)


// Add a list to another list.
def list = ['Groovy', 'Java']
assert ['Groovy', 'Java', 'Scala'] == [*list, 'Scala']



// Add a range to a list.
def range = 2..5
assert [1, 2, 3, 4, 5, 6] == [1, *range, 6]


// Add a map to another map.
def map = [name: 'mrhaki', blog: true]
assert [name: 'mrhaki', blog: true, subject: 'Groovy Goodness'] == [subject: 'Groovy Goodness', *:map]


// Little trick to simulate named parameters for a method.
// It is just a trick so the ordering of the map key/values
// must be the same as the method parameters.
def paramsMap = [
        n: 1,
        text: 'hello',
        date: new Date().parse("yyyy/MM/dd", "2009/09/04")
]
def paramsList = paramsMap.values().toList()
assert 'Say hello on 2009-09-04.\n' == new SpreadSimple().speak(*paramsList)
