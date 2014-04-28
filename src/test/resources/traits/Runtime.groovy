package traits

/**
 * User: jorgefrancoleza
 * Date: 28/04/14
 */

trait Extra {
    String extra() { "I'm an extra method" }
}
class Something {
    String doSomething() { 'Something' }
}

def s = new Something() as Extra
assert s.extra() == "I'm an extra method"
assert s.doSomething() == 'Something'

def extraMap = [] as Extra
assert extraMap.extra() == "I'm an extra method"