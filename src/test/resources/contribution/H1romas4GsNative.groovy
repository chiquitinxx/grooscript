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

class Test1  {
    @GsNative
    def calc() {/*
        return 1
    */}
}

class Test2 {
    @GsNative
    def calc() {/*
        return 2
    */}
}

// OK
assert(new Test1().calc() == 1)
// Assertion fails: (new Test2().calc() == 2) - false
assert(new Test2().calc() == 2)