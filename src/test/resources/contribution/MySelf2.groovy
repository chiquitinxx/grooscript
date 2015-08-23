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

import org.grooscript.asts.GsNative

class Container {
    def item

    def initItem(value) {
        item = value
        this
    }

    @GsNative
    static dirtyItem() {/*
        return [1, 2, 3, {one: 1}];
    */}

    def isNull() {
        if (item == null) {
            true
        } else {
            false
        }
    }
}

println 'Hola ' + Container.dirtyItem()
assert ([1, 2] + 'Hey ') == [1, 2, 'Hey ']
assert ('Hey ' + [1, 2]) == 'Hey [1, 2]'

assert new Container().isNull()
assert !new Container().initItem(5).isNull()
//Only works in js
assert !new Container().initItem(Container.dirtyItem()).isNull()
