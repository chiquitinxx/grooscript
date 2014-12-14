package doc

// tag::object[]
class MyObject {
    private static final NUMBER = 6
    def name = 'MyObject'
    Map map

    MyObject() {
        map = [:]
    }

    MyObject(name) {
        map = [newName: name]
        this.name = name
    }

    private addToMap(key, value) {
        map[key] = value
    }

    void register(listActions) {
        listActions.each {
            addToMap(it.name, it.data ?: NUMBER)
        }
    }
}
// end::object[]

assert new MyObject().map == [:]
assert new MyObject().name == 'MyObject'
assert new MyObject('coolName').map == [newName: 'coolName']
assert new MyObject('coolName').name == 'coolName'

def myObject = new MyObject()
myObject.register([[name: 'inc', data: 5], [name: 'event']])
assert myObject.map == [inc: 5, event: 6]