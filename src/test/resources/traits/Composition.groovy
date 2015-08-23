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
package traits

trait FlyAbility {
    String fly() { "I'm flying!" }
}
trait SpeakAbility {
    String speak() { "I'm speaking!" }
}

class Duck implements FlyAbility, SpeakAbility {}

def d = new Duck()
assert d.fly() == "I'm flying!"
assert d.speak() == "I'm speaking!"

class NewDuck implements FlyAbility, SpeakAbility {
    String name
    String quack() { "${name}: Quack!" }
    String speak() { quack() }
}

def nd = new NewDuck(name: 'Groovy')
assert nd.fly() == "I'm flying!"
assert nd.quack() == "Groovy: Quack!"
assert nd.speak() == "Groovy: Quack!"
