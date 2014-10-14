package advanced

/**
 * JFL 22/12/12
 */

v1 = 'alpha'; v2 = 'omega'
// this can done with explicit swapping via a temp variable
// or in a slightly more interesting way with a closure
swap = { temp = v1; v1 = v2; v2 = temp }
swap()
assert v1 == 'omega' && v2 == 'alpha'

assert 'string'.reverse() == 'gnirts'

string = 'Yoda said, "can you see this?"'
revwords = string.split(' ').toList().reverse().join(' ')
assert revwords == 'this?" see you "can said, Yoda'

words = ['bob', 'alpha', 'rotator', 'omega', 'reviver']
long_palindromes = words.findAll{ w -> w == w.reverse() && w.size() > 5 }
assert long_palindromes == ['rotator', 'reviver']
//Javascript stuff
//assert "%22" == "\""
assert "hello".length() == 5

assert 'hello' in ['bye','hello','hey']
assert 1 in 0..2

def a = ''
assert !a

a = '\n'
assert a

a = '\nHello\nBye\n'
assert a == '''
Hello
Bye
'''

assert [1,2,3,4].join('-') == '1-2-3-4'
assert ['a','b','c'].join() == 'abc'

aaa = '"bread","apple","egg"'
items = aaa.split(',')
assert items[1] == '"apple"'

def multiline = '''\
Groovy is closely related to Java,
so it is quite easy to make a transition.
'''

multiline.eachLine {
    if (it =~ /Groovy/) {
        assert it == 'Groovy is closely related to Java,'
    }
}

multiline.eachLine { line, count ->
    if (count == 0) {
        assert line == 'Groovy is closely related to Java,'
    } else {
        assert line == 'so it is quite easy to make a transition.'
    }
}

assert multiline.readLines().size() == 2

assert 'Groovy      ' == "Groovy".padRight(12)
assert '      Groovy' == /Groovy/.padLeft(12)

assert 'Groovy * * *' == "Groovy".padRight(12, ' *')
assert 'Groovy Groovy Groovy' == 'Groovy'.padLeft(20, 'Groovy ')

assert 'groovy ruby groovy'.count('groovy') == 2
assert '123'.isNumber()
assert '123 '.isNumber()
assert ' 123'.isNumber()
assert '123.2'.isNumber()
assert !'123 4'.isNumber()
assert !'12h3'.isNumber()
assert !''.isNumber()
assert !' '.isNumber()

assert 'hello'.capitalize() == 'Hello'
assert 'HELLO'.capitalize() == 'HELLO'

def languages = ['Groovy', 'Java', 'Clojure']
def multiGString = """\
My favourites languages:
${languages[0]}
${languages[1]}
${languages[2]}
"""

multiGString.eachLine { line, count ->
    if (count == 0) {
        assert line == 'My favourites languages:'
    }
    if (count == 1) {
        assert line == 'Groovy'
    }
    if (count == 2) {
        assert line == 'Java'
    }
    if (count == 3) {
        assert line == 'Clojure'
    }
}

assert "hello".inject('') { acc, val ->
    acc += val
} == 'hello'

assert "bye"[1] == 'y'

def ml = '''
1
2'''

assert ml[0] == '\n'
assert ml[1] == '1'
assert ml[2] == '\n'

assert 'a' as char == 97
