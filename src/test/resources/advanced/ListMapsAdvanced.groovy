package advanced

/**
 * User: jfl
 * Date: 13/01/13
 */

listA = [1,2,3]
assert listA.head() == 1
assert listA.pop() == 3
assert listA == [1,2]
assert listA.reverse() == [2,1]
assert listA == [1,2]
assert listA.reverse(true) == [2,1]
assert listA == [2,1]

def strings = [ 'a', 'b', 'c' ]
assert strings.take( 0 ) == []
assert strings.take( 2 ) == [ 'a', 'b' ]
assert strings.take( 5 ) == [ 'a', 'b', 'c' ]

def nums = [ 1, 3, 2 , 5, 0]
assert nums.takeWhile{ it < 0 } == []
assert nums.takeWhile{ it < 3 } == [ 1 ]
assert nums.takeWhile{ it < 4 } == [ 1, 3, 2 ]

map = [:].withDefault { 42 }
assert map.number == 42

def map2 = [a:1, b:2, c:3]
assert map2.inject([]) { list, k, v ->
    list + [k] * v
} == ['a', 'b', 'b', 'c', 'c', 'c']

assert map2.count { it.value > 1} == 2

assert [a:10, b:20] + [a:5, c:7] == [a:5, b:20, c:7]
assert [a:10, b:20] - [a:10, c:7] == [b:20]
