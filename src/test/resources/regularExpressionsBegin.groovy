/**
 * JFL 09/09/12
 */

assert "abc" == /abc/
assert "\\d" == /\d/
def reference = "hello"
assert reference == /$reference/
assert "\$" == /$/

assert "potatoe" ==~ /potatoe/
assert !("potatoe with frites" ==~ /potatoe/)

myFairStringy = 'The rain in Spain stays mainly in the plain!'
// words that end with 'ain': \b\w*ain\b
//BOUNDS = /\b/
//rhyme = /$BOUNDS\w*ain$BOUNDS/
found = ''
//(myFairStringy =~ rhyme).each { match ->
(myFairStringy =~ /\b\w*ain\b/).each { match ->
        found += match + ' '
}
assert found == 'rain Spain plain '

assert "griffon" =~ /gr*/
//This dont work :/ always return true that javascript object
//assert !("gradle" =~ /sup*/)