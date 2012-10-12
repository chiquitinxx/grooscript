/**
 * JFL 05/09/12
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