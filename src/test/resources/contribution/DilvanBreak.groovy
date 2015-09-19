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

String[] names = ["webgl", "experimental-webgl", "webkit-3d", "moz-webgl"]
def result = null;
for (String name : names) {
    break;
    result = '1'
}

assert result == null

for (aName in names) {
    result = aName
    break;
    result = null;
}

assert result == names[0]

def val = 0
for (int i = 0; i < names.size(); i++) {
    val++
    break;
}
assert val == 1