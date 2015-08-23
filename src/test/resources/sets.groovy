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

def set = [1,2] as Set
assert set.size() == 2

def set2 = new HashSet()
set2.add(1)
set2.add(2)

assert set == set2
assert !(set2 == [1,2])
assert set2.toList() == [1,2]

def s= [1,2] as Set
assert s.add(3)
assert ! s.add(2)
assert s.addAll( [5,4] )
assert ! s.addAll( [5,4] )
assert s == [1,2,3,5,4] as Set

assert set + s == [1,2,3,4,5] as Set
assert s - set == [3,4,5] as Set


