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

class ExecCall {

    CallItem callItem

    ExecCall(callItemT) {
        this.callItem = callItemT
        assert callItemT(3).number == 3
        assert callItemT(3).one == 1
        tryHere()
        assert callItem(3).number == 3
        assert callItem(3).one == 1
    }

    def tryHere() {
        assert callItem(3).number == 3
        assert callItem(3).one == 1
    }
}

class CallItem {
    def call(number) {
        [one: 1, two: 2, number: number]
    }
}

new ExecCall(new CallItem())