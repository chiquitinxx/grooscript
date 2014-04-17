package traits

/**
 * Created by jorge on 17/04/14.
 */

trait SpeakingDuck {
    String speak() { quack() }
}
class CoolDuck implements SpeakingDuck {
    String methodMissing(String name, args) {
        "${name.capitalize()}!"
    }
}
def d = new CoolDuck()
assert d.speak() == 'Quack!'

trait DynamicObject {
    private Map props = [:]
    def methodMissing(String name, args) {
        name.toUpperCase()
    }
    def propertyMissing(String prop) {
        props['prop']
    }
    void setProperty(String prop, Object value) {
        props['prop'] = value
    }
}

class MyDynamic implements DynamicObject {
    String existingProperty = 'ok'
    String existingMethod() { 'ok' }
}
def md = new MyDynamic()
assert md.existingProperty == 'ok'
assert md.foo == null
md.foo = 'bar'
assert md.foo == 'bar'
assert md.existingMethod() == 'ok'
assert md.someMethod() == 'SOMEMETHOD'
