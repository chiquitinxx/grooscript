package advanced

/**
 * JFL 12/10/12
 */
class Summer {

    def sumWithDefaults(a, b, c=0){
        return a + b + c

    }
    def sumWithList(List args){
        return args.inject(0){sum,i -> sum += i}

    }
    def sumWithOptionals(a, b, Object[] optionals){
        return a + b + sumWithList(optionals.toList())

    }
    def sumNamed(Map args){
        ['a','b','c'].each{args.get(it,0)}
        return args.a + args.b + args.c
    }
}

def summer = new Summer()
assert 2 == summer.sumWithDefaults(1,1)
assert 3 == summer.sumWithDefaults(1,1,1)
assert 2 == summer.sumWithList([1,1])
assert 3 == summer.sumWithList([1,1,1])

assert 2 == summer.sumWithOptionals(1,1)
assert 3 == summer.sumWithOptionals(1,1,1)
assert 8 == summer.sumWithOptionals(1,1,1,5)
assert 2 == summer.sumNamed(a:1, b:1)
assert 3 == summer.sumNamed(a:1, b:1, c:1)
assert 1 == summer.sumNamed(c:1)

assert 3 * 3 * 3 * 4 == [ 3, 3, 3, 4 ].inject { acc, val -> acc * val }
assert 3 * 3 * 3 * 4 == [ 3, 3, 3, 4 ].inject { acc, val -> acc * val }
assert 3 * 3 * 3 * 4 == [ 3, 3, 3, 4 ].inject(1) { acc, val -> acc * val }
assert 'b' == ([['a','b'], ['b','c'], ['d','b']].inject { acc, val -> acc.intersect( val ) })[0]

assert 1*1*2*3*4 == [1,2,3,4].inject(1) { acc, val -> acc * val }
assert 0+1+2+3+4 == [1,2,3,4].inject(0) { acc, val -> acc + val }

assert 'The quick brown fox' ==
        ['quick', 'brown', 'fox'].inject('The') { acc, val -> acc + ' ' + val }

assert 'bat' ==
        ['rat', 'bat', 'cat'].inject('zzz') { min, next -> next < min ? next : min }

def max = { a, b -> [a, b].max() }
def animals = ['bat', 'rat', 'cat']
assert 'rat' == animals.inject('aaa', max)