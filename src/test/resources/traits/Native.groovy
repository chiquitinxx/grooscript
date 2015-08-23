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

import org.grooscript.asts.GsNative

trait NumberAbility {
    @GsNative
    int giveMeFive() {/*
        return 5;
    */}

    @GsNative
    int doubleMe(num) {/*
        return num * 2;
    */}
}

class MyNative implements NumberAbility {}
def myNative = new MyNative()

assert myNative.giveMeFive() == 5
assert myNative.doubleMe(7) == 14

