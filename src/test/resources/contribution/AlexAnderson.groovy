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

def listOfLists = [[1,2,3], [4,5,6]]
def middle = []
listOfLists.each { a, b, c ->
    //println "Middle value is $b"
    middle << b
}

assert middle.size() == 2
assert middle[0] == 2
assert middle[1] == 5


def result
['tim', 19].with { String name, int age ->

    result = "$name is $age years old"
    //println 'r->'+result
}

assert result == "tim is 19 years old"
