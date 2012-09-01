/**
 * JFL 01/09/12
 */

class MyClass {
    def name
    def numberMessages = 0

    MyClass(startingName) {
        name = startingName
    }

    MyClass() {
        name = ''
    }

    def saySomething(message) {
        println "${name}: ${message}"
        numberMessages++
    }
}

myClass = new MyClass()

assert myClass.numberMessages == 0
assert !myClass.name

myClass.name = 'Fan'
myClass.saySomething('GroovyRocks')

assert myClass.numberMessages == 1
assert myClass.name == 'Fan'
