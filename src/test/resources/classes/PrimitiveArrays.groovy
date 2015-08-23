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

String[] gr8 = new String[2]
gr8.putAt(0, 'Groovy')
gr8.putAt(1, 'Grails')

assert gr8 == ['Groovy', 'Grails']

byte[] anArrayOfBytes = new byte[1]
anArrayOfBytes[0] = 100.byteValue()
assert anArrayOfBytes == [100]
short[] anArrayOfShorts = new short[1]
anArrayOfShorts[0] = 90.shortValue()
assert anArrayOfShorts == [90]
long[] anArrayOfLongs = new long[1]
anArrayOfLongs[0] = 80.longValue()
assert anArrayOfLongs == [80]
float[] anArrayOfFloats = new float[1]
anArrayOfFloats[0] = 70.floatValue()
assert anArrayOfFloats == [70]
double[] anArrayOfDoubles = new double[1]
anArrayOfDoubles[0] = 60.doubleValue()
assert anArrayOfDoubles == [60]
boolean[] anArrayOfBooleans = new boolean[1]
anArrayOfBooleans[0] = false
assert anArrayOfBooleans == [false]
char[] anArrayOfChars = new char[1]
anArrayOfChars[0] = 'a'
assert anArrayOfChars == ['a']


