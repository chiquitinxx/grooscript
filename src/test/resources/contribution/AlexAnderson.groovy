package contribution

/**
 * JFL 18/10/12
 */

def listOfLists = [[1,2,3], [4,5,6]]
def middle = []
listOfLists.each { a, b, c ->
    //println "Middle value is $b"
    middle << b
}

assert middle.size() == 2
assert middle[0] == 2
assert middle[1] == 5


def result
['tim', 19].with { String name, int age ->

    result = "$name is $age years old"
    //println 'r->'+result
}

assert result == "tim is 19 years old"
