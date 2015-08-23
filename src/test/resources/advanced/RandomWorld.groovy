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

final MAX = 5

def randomNumber
def surprise = false
def values = [:]

500.times {
    randomNumber = new Random().nextInt(MAX)
    if (randomNumber>=MAX) {
        surprise = true
    }
    values."$randomNumber" = (values."$randomNumber"?values."$randomNumber"+1:1)
}

values.each { key, value ->
    println "$key repeated $value times"
}

assert !surprise

result = [heads:0, tails:0]
500.times {
    if (new Random().nextBoolean()) {
        result.heads++
    } else {
        result.tails++
    }
}

println (result.heads>result.tails?'Next bid on TAILS':(result.heads==result.tails?'WHO KNOWS!':'Next bid on HEADS'))
println " h:${result.heads} t:${result.tails}"

def r = new Random().nextInt(4)
assert r>=0 && r<4