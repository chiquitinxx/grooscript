
/**
 * User: jfl
 * Date: 13/01/13
 */

def set = [1,2] as Set
assert set.size() == 2

def set2 = new HashSet()
set2.add(1)
set2.add(2)

assert set == set2
assert !(set2 == [1,2])
assert set2.toList() == [1,2]

def s= [1,2] as Set
assert s.add(3)
assert ! s.add(2)
assert s.addAll( [5,4] )
assert ! s.addAll( [5,4] )
assert s == [1,2,3,5,4] as Set

assert set + s == [1,2,3,4,5] as Set
assert s - set == [3,4,5] as Set


