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
// tag::mark[]
assert 0.1 + 0.2 == 0.3
assert 1.1 + 0.2 == 1.3
assert 0.1G + 0.2G == 0.3G
assert 4 * 5 + 45 % 4 == 21

assert 5 / 2 == 2.5
assert 5 % 2 == 1
assert 4 ** 3 == 64
assert -(-5) == 5
assert +(-5) == -5
assert Math.PI > 3.14159
assert Math.PI < 3.1416
// end::mark[]