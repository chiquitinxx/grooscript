/**
 * JFL 02/09/12
 */
def list = [5, 6, 7, 8]
assert list.get(2) == 7
assert list[2] == 7
//assert list instanceof java.util.List

def emptyList = []
assert emptyList.size() == 0
emptyList.add(5)
assert emptyList.size() == 1