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
package doc

// tag::mark[]
class Magic {
    def data = [:]
    void setProperty(String name, value) {
        data[name] = value
    }
    def getProperty(name) {
        data[name]
    }
}
Magic.metaClass.getAllData = { ->
    data
}

def magic = new Magic()
magic.a = 5
magic.allData == [a: 5]

magic.metaClass.methodMissing = { String name, args ->
    name
}

assert magic.Groovy() == 'Groovy'
// end::mark[]