package advanced

/**
 * User: jfl
 * Date: 14/01/13
 */

class MyObject {
    def a
    static b
    def method1() {
        println 'method1'
    }

    static prod(a,b) {
        return a*b
    }
}

def myObject = new MyObject()

println 'Properties->'+myObject.properties
//Return at least 2
assert myObject.properties.size() >= 2
assert myObject.properties == [class: MyObject, b:null, a:null]

myObject.metaClass.methods.each { MetaMethod method->
    println 'name method->'+method.name
}
//We gonna return at least 2 in javascript
assert myObject.metaClass.methods.size() >= 2

myObject.invokeMethod('method1',null)
assert myObject.invokeMethod('prod',[5,6]) == 30
