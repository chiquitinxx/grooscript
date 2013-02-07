package contribution

/**
 * User: jorgefrancoleza
 * Date: 07/02/13
 */

def a = 1
def b = 5.3
println "Hello World! 1 + 2 = ${a + b}"

assert a + b == 6.3
//println [1,2,3].join(", ")
//println 1,2
//println [1,2,3]

assert [1,2,3].join(", ") == "1, 2, 3"

assert [1,2,3,4,5] == [1,[2,3],[[4]],[],5].flatten()
assert [0,8,8,5] == [0,8,[8,5]].flatten()

//println ([0,8,[8,5]].flatten())
//println ([1,[2,3],[[4]],[],5].flatten())

//Eval not supported
//println Eval.me([name:'kanemu',age:"39"])
//assert 10 == Eval.me(' 2 * 4 + 2')
//assert 10 == Eval.x(2, ' x * 4 + 2')
