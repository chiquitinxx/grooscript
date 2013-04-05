package contribution

/**
 * Problems with maps and closures in maps
 *
 * @author Jorge Franco
 */

def a = [a:1]
def b = [b:2, c:3]

assert a << b == [a:1, b:2, c:3]
assert a + b == [a:1, b:2, c:3]
def map = a << b
assert map.size() == 3, "map size is ${map.size()}"
def map2 = a + b
assert map2.size() == 3, "map2 size is ${map2.size()}"
a.putAll(b)
assert a.size() == 3, "a size is ${a.size()}"

def one = [start: { println 'start'}]
def two = [second: { println 'second'}]
def three = [third: { println 'third'}, four: {println 'four'}]
def all = one << two << three

assert all.size() == 4, "all size is ${all.size()}"
all.each { it.value() }
