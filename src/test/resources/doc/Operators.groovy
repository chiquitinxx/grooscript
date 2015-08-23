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
package doc

// tag::operators[]
def compare = { a,b -> a <=> b }
assert compare(2, 1) == 1 && compare(1, 1) == 0 && compare(1, 2) == -1

class Item {
    def value
    def config = [:]
    def getValue() {
        1
    }
    boolean equals(other) {
        this.config == other.config
    }
}
def item = new Item(value: 5)
assert item.value == 1
assert item.@value == 5

assert 5 in [1, 9, 5]
assert !(2 in [1])

def otherItem = new Item(value: 5)
assert item == otherItem
assert !(item.is(otherItem))

assert item?.config?.other == null
assert 5 == item?.config?.data ?: 5
// end::operators[]