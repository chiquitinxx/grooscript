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
def numberOfTimes = 0

gettingTime = { closure ->

    numberOfTimes ++;
    before = new Date().time
    //Strange, have to add this, or milisec return 0 always in javascript
    before--
    closure()
    milisecs = new Date().time - before

}

def map = [:]
assert map.size() == 0

map['time'] = gettingTime {
    map.put('time',0)
    map.put('me','Jorge')
}

assert numberOfTimes==1
assert map['time']>0
assert map.size() == 2

map["numbers"]=[]
map["numbers"] << 5
assert map["numbers"].size()==1
map["numbers"].add([1:1 , 2:22])
assert map["numbers"].size()==2
assert map["numbers"][1][2] == 22

map = [one:1,two:2,three:3]
def keys='',values=0
map.each { key,value ->
    keys+=key
    values+=value
}
assert keys=='onetwothree'
assert values == 6
map.each { element ->
    values+=element.value
}
assert values == 12
assert map.get('one') == 1
assert !map.containsKey('five')
assert map.get('five',5) == 5
assert map.containsKey('five')
assert map.containsKey('two')
assert map.containsValue(3)

map = [one:1,two:2,three:3]
def mapValues = map.values()
assert map.values().toList() == [1,2,3]