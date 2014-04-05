package traits

/**
 * User: jorgefrancoleza
 * Date: 05/04/14
 */

trait FlyingAbility {
    String fly() { "I'm flying!" }
}

class Bird implements FlyingAbility {}
class CrazyGuy implements FlyingAbility {}
def b = new Bird()
assert b.fly() == "I'm flying!"
assert new CrazyGuy().fly() == "I'm flying!"

