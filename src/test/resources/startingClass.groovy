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

    def tellMeNumber() {
        def name = 'Name'
        def numberMessages = -5
        assert name == 'Name'
        assert numberMessages == -5
        return this.numberMessages
    }
}

myClass = new MyClass()

assert myClass.numberMessages == 0
assert !myClass.name
assert myClass.name == ''

myClass.name = 'Fan'
myClass.saySomething('GroovyRocks')

assert myClass.numberMessages == 1
assert myClass.name == 'Fan'
assert myClass.tellMeNumber() == 1

def myClass2 = new MyClass('George')
assert myClass2.name == 'George'