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

def repeater(times) {
      { value ->
          def result = ''
          times.times { result+= value}
          return result
      }
}

assert repeater(2).call('mrhaki') == 'mrhakimrhaki'

assert repeater(2)('mrhaki') == 'mrhakimrhaki'

def newRepeater = { times, transformer = { it } ->
    { value ->
        def result = ''
        times.times { result+= transformer(value)}
        return result
    }
}

assert newRepeater(2).call('mrhaki') == 'mrhakimrhaki'
assert newRepeater(2)('mrhaki') == 'mrhakimrhaki'
assert newRepeater(2) { it.toUpperCase() } ('mrhaki') == 'MRHAKIMRHAKI'
assert newRepeater(2, { it.reverse() })('mrhaki') == 'ikahrmikahrm'