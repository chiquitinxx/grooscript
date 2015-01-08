package contribution

/**
 * JFL 06/12/12
 */

def list = [true,false]

assert list[0]
assert !list[1]
// tag::mark[]
def res

switch ([true,false]) {
    case {it[0]}: res = 0; break
    case {it[1]}: res = 1; break
    default : res = 2
}

assert res == 0
// end::mark[]

def f = "fizz"
def b = "buzz"
def count1 = 0
def count2 = 0
(1..100).each{ x ->
    count1++
    count2++
    switch ([count1 == 3, count2 == 5]) {
        case{it[0]&&it[1]} : str= f+b;count1=0;
            count2=0; break
        case{it[0]} : str=f;count1=0; break
        case{it[1]} : str=b;count2=0; break
        default : str=x
    }
    println str
}
