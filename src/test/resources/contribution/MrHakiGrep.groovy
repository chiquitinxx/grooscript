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

assert ['Groovy'] == ['test', 'Groovy', 'Java'].grep(~/^G.*/) , 'Pattern match'
assert ['b', 'c'] == ['a', 'b', 'c', 'd'].grep(['b', 'c']), 'List contains'
assert [15, 16, 12] == [1, 15, 16, 30, 12].grep(12..18), 'Range contains'
assert [42.031] == [12.300, 109.20, 42.031, 42.032].grep(42.031), 'Object equals'
assert [100, 200] == [10, 20, 30, 50, 100, 200].grep({ it > 50 }), 'Closure boolean'