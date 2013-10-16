package contribution

/**
 * User: jorgefrancoleza
 * Date: 16/10/13
 */
class Example {
    def greet() {
        return { String it -> "Hello" + it }
    }
}

Example e = new Example()
def a = e.greet();
assert a("Foo") == "HelloFoo"
//println a("Foo") console.log("hi")