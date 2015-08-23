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
package advanced

class Missing {

    def a

    def countMissing = 0

    def methodMissing(String name,args) {
        countMissing++
        this.metaClass."$name" = { ->
            a++
        }
    }

    Missing() {
        def mc = new ExpandoMetaClass(Missing, false, true)
        mc.initialize()
        this.metaClass = mc
    }


}

def missing = new Missing()

missing.a = 5

assert missing.getA() == 5
assert missing.countMissing == 0


missing.doSomething()

assert missing.countMissing == 1


missing.doSomething()
assert missing.countMissing == 1
assert missing.a == 6

def expandoMissing = new Expando()
count = 0
expandoMissing.metaClass.methodMissing = { String name,args ->
    count++
}

expandoMissing.doSomething()
assert count == 1