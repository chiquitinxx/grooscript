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
package features

import org.grooscript.asts.GsNotConvert
import org.grooscript.asts.GsNative

@GsNotConvert
class Hide {
    def secret
}

class Master {
    @GsNotConvert
    def secretAction() {
        println 'Super Secret stuff'
    }

    @GsNative
    def alert(message) {/*
        alert(message);
    */}
}




