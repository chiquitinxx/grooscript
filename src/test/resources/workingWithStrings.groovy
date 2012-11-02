/**
 * JFL 12/09/12
 */
def one = "one"
def one2 = 'one'
def one3 = '''
one
'''

assert one == one2
assert one == one3.trim()

date = new Date(0)
out  = "Year $date.year Month $date.month Day $date.date"
assert !out.contains('undefined')

out = "my 0.02\$"
assert out == 'my 0.02$'

assert "Groovy".startsWith("G")

def greeting = 'Hello Groovy!'
assert greeting.indexOf('Groovy') >= 0
assert greeting[6..11]  == 'Groovy'
assert greeting.count('o') == 3

assert greeting.size() == 13

def numbers = '1 2 4 2 6 3 1'
out = numbers.replaceAll('1','one')
assert numbers == '1 2 4 2 6 3 1'
assert out == 'one 2 4 2 6 3 one'

assert 'one two'.tokenize() == ['one','two']
assert 'one two'.split(" ")[0] == 'one'