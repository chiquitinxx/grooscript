package advanced

/**
 * User: jfl
 * Date: 23/01/13
 */

def (four,five) = [4,5]
assert four == 4
assert five == 5

class Point { float x, y }
Point.metaClass.getAt = { pos ->
    pos == 0 ? x:y
}
def p = new Point(x: 1, y: 2)

def (a, b) = p

assert a == 1
assert b == 2

def (_, month, year) = "18th June 2009".split()
assert "In $month of $year" == "In June of 2009"

def (r, s, t) = [1, 2]
assert r == 1 && s == 2 && t == null

class ItemWithMethod {
    private return12() {
        [1,2]
    }
    def doSomething() {
        def (first, second) = return12()
        [first, second]
    }
}

def (one, two) = new ItemWithMethod().doSomething()
assert one == 1
assert two == 2
