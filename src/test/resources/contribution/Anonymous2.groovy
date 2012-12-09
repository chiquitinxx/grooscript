package contribution

/**
 * JFL 06/12/12
 */

(1..20).each{
    s=(it%3==0?"fizZ":"")+(it%5==0?"buzZ":"")
    println((s)?s:it)
    //Not allowed
    //println((s=(it%3==0?"fizZ":"")+(it%5==0?"buzZ":""))?s:it)
}

//assert ![]
//assert ![:]

def item

assert !item

def map = [:]
map.one = 'one'
map.two = true
map.three = false

assert map.one
assert map.two
assert !map.three
assert !map.four