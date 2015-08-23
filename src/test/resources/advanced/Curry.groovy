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

def touchNumbers = { a, b, c ->
    (a*b)+c
}

def touchOneTwo = touchNumbers.curry(1, 2)

assert touchOneTwo(3) == 5

def touchToFourFive = touchNumbers.rcurry(4, 5)

assert touchToFourFive(2) == 13

def touchOneTwoN = touchNumbers.ncurry(1, 5, 4)

assert touchOneTwoN(2) == 14

def otherTouch = touchNumbers.ncurry(2, 3)

assert otherTouch(10,5) == 53

def midTouch = touchNumbers.ncurry(1, 5)

assert midTouch(10,3) == 53
