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
package web

trait RandomAbility {
    def methodMissing(String name, args) {
        if (name.startsWith('random')) {
            def number = name.substring(6) as int
            new Random().nextInt(number)
        } else {
            throw new RuntimeException()
        }
    }
}

class MyNumbers implements RandomAbility {
}

def myNumbers = new MyNumbers()

100.times {
    assert myNumbers.random10() < 10
}