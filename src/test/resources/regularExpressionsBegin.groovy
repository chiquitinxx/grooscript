/**
 * JFL 09/09/12
 */
// tag::regs[]
assert "abc" == /abc/
assert "\\d" == /\d/
def reference = "hello"
assert reference == /$reference/
assert "\$" == /$/

assert "potatoe" ==~ /potatoe/
assert !("potatoe with frites" ==~ /potatoe/)

myFairStringy = 'The rain in Spain stays mainly in the plain!'

found = ''
(myFairStringy =~ /\b\w*ain\b/).each { match ->
        found += match + ' '
}
assert found == 'rain Spain plain '

def matcher = 'class A{ class B {}}' =~ /\bclass\s+(\w+)\s*\{/
assert matcher.size() == 2
// end::regs[]
assert "griffon" =~ /gr*/
//This doesn't work :/ always return true that javascript object
//assert !("gradle" =~ /sup*/)
