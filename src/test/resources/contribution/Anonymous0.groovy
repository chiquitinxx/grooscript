package contribution

/**
 * JFL 05/12/12
 */

def rockstar
def defaultrockstar = rockstar ?: "Elvis Presley"
assert defaultrockstar == "Elvis Presley"

def other = 'Queen'
other = other ? 'Queen 2' : 'Queen 3'
assert other == 'Queen 2'

assert '1'*0 == ''
assert '1'*0.9 == ''
assert !('1'*0)
//println '1'*2
assert '1'*2 == '11'


100.times{it->
    //println"->${(it%3/2)}"
    println'Fizz'*((it%3)/2)+'Buzz'*((it%5)/4)?:++it
}

