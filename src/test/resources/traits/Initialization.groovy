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
package traits

trait StaticFields {
    static VALUE = 0
    static LIST = [1, 2]
    static EMPTY
    static EMPTY_2

    static initStatic() {
        VALUE = 0
        LIST = [1, 2]
    }

    def init() {
        VALUE = 0
        LIST = [1, 2]
        EMPTY = null
    }
}

trait StaticColors {
    static COLORS = ['red', 'blue']
}

class WithStaticFields implements StaticFields, StaticColors {
    def add() {
        EMPTY_2 = 1
        LIST << VALUE
    }
}

def checkAll = {
    assert WithStaticFields.VALUE == 0
    assert WithStaticFields.LIST == [1, 2]
    assert WithStaticFields.EMPTY == null
    assert new WithStaticFields().add() == [1, 2, 0]
    assert WithStaticFields.EMPTY == null
    assert WithStaticFields.EMPTY_2 == 1
    assert WithStaticFields.COLORS == ['red', 'blue']
}
checkAll()
WithStaticFields.initStatic()
checkAll()
def withStaticFields = new WithStaticFields()
withStaticFields.init()
checkAll()

