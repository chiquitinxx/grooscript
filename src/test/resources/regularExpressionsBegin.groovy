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
BOUNDS = "\\b"
rhyme = "${BOUNDS}\\w*ain${BOUNDS}"
found = ''
//(myFairStringy =~ rhyme).each { match ->
(myFairStringy =~ /\b\w*ain\b/).each { match ->
        found += match + ' '
}
assert found == 'rain Spain plain '

/*
myRegularExpression = /([a-zA-Z]+), ([a-zA-Z]+): ([0-9]+). ([0-9]+). ([0-9]+). ([A-Z]) ([0-9]+). ([0-9]+). ([0-9]+)./

twister = 'she sells sea shells at the sea shore of seychelles'
//def what = twister =~ /s.e/
def what = twister =~ /y\doyo/
println what
assert what
*/