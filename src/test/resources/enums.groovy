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
// tag::enums[]
enum Days {
    monday, tuesday, wednesday, thursday, friday, saturday, sunday

    static tellMeOne() {
        'one'
    }
}

Days a = Days.monday

assert a == Days.monday
assert 'one' == Days.tellMeOne()
assert Days.monday.name() == 'monday'
assert a.name() == 'monday'
assert a.ordinal() == 0
assert Days.sunday.ordinal() == 6
// end::enums[]