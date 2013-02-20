package advanced

/**
 * User: jorgefrancoleza
 * Date: 20/02/13
 */

def a = [1,2,3,4,5]*.multiply(2)
println a*.power(2)
assert a == [2, 4, 6, 8, 10]

def list = [12, 20, 34]
def result = list.collectMany { [it, it*2, it*3] }

assert result == [12,24,36,20,40,60,34,68,102]
assert list == [12, 20, 34]

list = [1,2,3,5,7,2,9]

assert [1,2,3] == list.takeWhile { it < 5 }
assert [5,7,2,9] == list.dropWhile { it < 5 }