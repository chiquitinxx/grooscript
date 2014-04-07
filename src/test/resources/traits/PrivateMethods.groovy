package traits

/**
 * User: jorgefrancoleza
 * Date: 06/04/14
 */

trait Greeter {
    private String greetingMessage() {
        'Hello from a private method!'
    }
    String greet() {
        def m = greetingMessage()
        println m
        m
    }
}
class GreetingMachine implements Greeter {}
def g = new GreetingMachine()

assert g.greet() == "Hello from a private method!"

trait Introspector {
    def whoAmI() { this }
}
class Foo implements Introspector {}
def foo = new Foo()

assert foo.whoAmI().is(foo)
