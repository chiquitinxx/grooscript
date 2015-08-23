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

class SomeClass {

    public fieldWithModifier
    String typedField

    def untypedField
    protected field1, field2, field3
    private assignedField = new Date()
    static classField

    public static final String CONSTA = 'a', CONSTB = 'b'
    def someMethod(){
        def localUntypedMethodVar = 1

        int localTypedMethodVar = 1

        def localVarWithoutAssignment, andAnotherOne
    }
}

def localvar = 1

boundvar1 = 1

def someMethod(){

    localMethodVar = 1

    boundvar2 = 1

}

class Counter {

    public count = 0

}
def counter = new Counter()
counter.count = 1

assert counter.count == 1

def fieldName = 'count'

counter[fieldName] = 2

assert counter['count'] == 2
