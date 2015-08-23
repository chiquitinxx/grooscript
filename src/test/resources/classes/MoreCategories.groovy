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

// tag::mark[]
final class Distance {
    def number
    String toString() { "${number}m" }
}

class NumberCategory {
    static Distance getMeters(Number self) {
        new Distance(number: self)
    }
}

use(NumberCategory) {
    def dist = 300.meters

    assert dist instanceof Distance
    assert dist.toString() == "300m"
}
// end::mark[]
