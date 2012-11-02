package advanced

/**
 * JFL 02/11/12
 */
def foo(n) {
    return {n += it}
}
def accumulator =  foo(1)
assert accumulator(2) == 3
assert accumulator(1) == 4

def List list = [1, 2, 3].collect{
    if (it%2 == 0) return it * 2
    return it
}
assert list == [1,4,3]

def Map map
assert !map
map = [a:1, b:2, c:3, d:4]
assert !map.f

assert map.any { it.value > 3}
assert map.any { it.key == 'c'}
assert !map.any { key, value -> value > 4}
assert map.every { it.value < 6}

assert map.find { key, value -> value==3}.key == 'c'
assert map.findAll { it.value>1} == [b:2, c:3, d:4]

assert map.collect { it.value*it.value} == [1, 4, 9, 16]
assert map.collect { key, value -> key} == ['a', 'b', 'c', 'd']

assert !map.findAll{ it.value>8}
def m0 = [:]
assert m0.isEmpty()
//This not works in javascript
//assert !m0

//def l0 = []
//assert !l0

assert list.any { it > 2}
assert !list.any { it > 7}
assert !list.every { it > 2}
assert list.every { it < 7}

assert list.join('-') == '1-4-3'

def text = {
    def result = ''
    "mrhaki".size().times {
        result += it
    }
    result
}.call()

assert text == '012345'

