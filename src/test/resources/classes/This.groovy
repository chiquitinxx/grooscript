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

class WithThis {
    private String info = 'Data'

    String getInfo() {
        return this.info
    }

    void setInfo(info) {
        this.info = info
    }
}

assert new WithThis().getInfo() == 'Data'
assert new WithThis().info == 'Data'
def info = new WithThis(info: 'Info')
assert info.info == 'Info'
info.setInfo('NewInfo')
assert info.info == 'NewInfo'
assert info.getInfo() == 'NewInfo'
info.info = 'Back'
assert info.info == 'Back'