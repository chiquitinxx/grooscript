package traits

/**
 * User: jorgefrancoleza
 * Date: 05/04/14
 */

trait FlyingAbility {
    String fly() { "I'm flying!" }
    String sayHello(who) { "Hello ${who}!"}
}

class Bird implements FlyingAbility {}
class CrazyGuy implements FlyingAbility {}
def b = new Bird()
assert b.fly() == "I'm flying!"
assert new CrazyGuy().fly() == "I'm flying!"
assert b.sayHello('Grooscript') == "Hello Grooscript!"

