package advanced
/**
 * User: jfl
 * Date: 07/01/13
 */

assert 1 == 1.0
assert 1.1 > 1
assert "a" > "A"
assert "B" > "A"
assert "hello" > "Hello"
assert 6
def date = new Date()
assert date + 1 > date

def oneDate = new Date(87398475634)
assert oneDate.format('dd-MM-yyyy') == '08-10-1972'
// In CI server HH in format is distinct. I suppose hour depends of machine
assert oneDate.format('dd-MM-yy mm:ss') == '08-10-72 21:15'
assert oneDate.time == 87398475634