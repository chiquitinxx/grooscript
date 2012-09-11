/**
 * JFL 08/09/12
 */
def a = 0
if (true) a = 1 else a = -1
assert a == 1
(a > 0) ? (a = 3) : (a = 0)
assert a == 3

def name = 'Groovy'
a = 0

switch (name) {
    case 'JavaScript': a = -1; break
    case 'GROOVY': a = 1; a = 2;break
    case 'Groovy':a = 3; break
    default : a = 4
}

assert a == 3

a=0

while (a<5) {
    a = a + 2
    a--
}
assert a == 5

def store = ''
for (i in [1, 2, 3]) {
    store += i
}
assert store == '123'