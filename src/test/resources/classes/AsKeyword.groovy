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

def contacts = [] as Set
contacts << 'hello' << 'hello'

assert contacts.size() == 1

public interface Game {
    boolean start()
}

def game = [
        start : { -> println 'Game started.'; true }
] as Game

def beginGame = { Game newGame ->
    newGame.start()
}

assert beginGame(game) == true

String[] s = ["a", "b"] as String[]
assert s == ['a', 'b']

assert "${s[0]}${s[1]}c" as String == 'abc'



