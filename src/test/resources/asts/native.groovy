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
package asts

import org.grooscript.asts.GsNative

class Data {

    @GsNative def String saySomething(some){/*return some;*/}
    @GsNative sayOne(){/*return 1;*/}
    @GsNative public static int sayTwo(){/*return 2;*/}
    @GsNative
    static sayThree() { /*
        return 3;
    */}
    @GsNative
    def sayTrue() { /*
        return true;
    */}

    def static sayTen() {
        return 10;
    }
}

data = new Data()
//This fail in groovy, but have to work in javascript
assert data.saySomething('hello') == 'hello'
assert data.sayTrue()
assert data.sayOne() == 1
assert data.sayTwo() == 2
assert Data.sayThree() == 3
assert Data.sayTen() == 10