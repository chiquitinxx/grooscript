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

class One {

}

class Two extends One {

}

assert new One().class.name == 'classes.One'

assert new One().class.simpleName == 'One'
assert new One().class.superclass.name == 'java.lang.Object'
assert new One().class.superclass.simpleName == 'Object'

assert new Two().class.superclass.name == 'classes.One'

/*
assert "hello".class.name == 'java.lang.String'
assert 3.class.name == 'java.lang.Integer'
assert (3.02).class.name == 'java.math.BigDecimal'

assert [].class.name == 'java.util.ArrayList'
def map = [hello:5]
assert map.hello.class.name == 'java.lang.Integer'
*/
