/**
 * JFL 03/09/12
 */
def list = [1,2]
assert list.reverse()[0] == 2
list << 3
assert list.size() == 3
assert list.sort()[0] == 1

list = [1,'A',3]
list.remove(2)
list.remove('A')
assert list.size() == 1
assert list[0] == 1

list = ['a','b','c','b','d','c']
list.removeAll(['b','c'])
assert list.size() == 2
assert list[1] == 'd'

def doubled = [1,2,3].collect{ item ->
    item*2
}
//assert doubled == [2,4,6]
assert doubled[0] == 2
assert doubled[1] == 4
assert doubled[2] == 6
assert doubled.size() == 3

def odd = [1,2,3].findAll{ item ->
    item % 2 == 1
}
assert odd.size() == 2
assert odd[0] == 1
assert odd[1] == 3

assert [1,2] + [3,5] == [1,2,3,5]
assert [1,2,3,4,5] - [3,5] == [1,2,4]

list = [1, 2, 2, 3, 4]
assert list.unique() == [1, 2, 3, 4]
list = [1, 2, 2, 3, 4]
def uniqList = list.unique(false)
assert list == [1, 2, 2, 3, 4]
assert uniqList == [1, 2, 3, 4]
