package traits

/**
 * Created by jorge on 17/04/14.
 */

trait FlyAbility {
    String fly() { "I'm flying!" }
}
trait SpeakAbility {
    String speak() { "I'm speaking!" }
}

class Duck implements FlyAbility, SpeakAbility {}

def d = new Duck()
assert d.fly() == "I'm flying!"
assert d.speak() == "I'm speaking!"

class NewDuck implements FlyAbility, SpeakAbility {
    String name
    String quack() { "${name}: Quack!" }
    String speak() { quack() }
}

def nd = new NewDuck(name: 'Groovy')
assert nd.fly() == "I'm flying!"
assert nd.quack() == "Groovy: Quack!"
assert nd.speak() == "Groovy: Quack!"
