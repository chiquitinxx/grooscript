package traits

/**
 * User: jorgefrancoleza
 * Date: 06/04/14
 */

interface Named {
    String name()
}
trait Greetable implements Named {
    String greeting() { "Hello, ${name()}!" }
}
class Person implements Greetable {
    String name() { 'Bob' }
}

def p = new Person()
assert p.greeting() == 'Hello, Bob!'
assert p instanceof Named
assert p instanceof Greetable
