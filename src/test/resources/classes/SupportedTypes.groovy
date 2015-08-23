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

assert new String('hello') == 'hello'
assert new String() == ''
assert new HashMap() == [:]
assert new ArrayList() == []
assert new BigDecimal(7.00) == 7
assert new BigDecimal('7.00') == 7
assert new Float('7.00') == 7
assert new Float(5) == 5
assert new Double('7.00') == 7
assert new Double(5) == 5
assert new Integer('7') == 7
assert new Integer(5) == 5