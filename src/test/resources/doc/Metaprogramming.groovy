package doc

/**
 * Created by jorgefrancoleza on 8/1/15.
 */
// tag::mark[]
class Magic {
    def data = [:]
    void setProperty(String name, value) {
        data[name] = value
    }
    def getProperty(name) {
        data[name]
    }
}
Magic.metaClass.getAllData = { ->
    data
}

def magic = new Magic()
magic.a = 5
magic.allData == [a: 5]

magic.metaClass.methodMissing = { String name, args ->
    name
}

assert magic.Groovy() == 'Groovy'
// end::mark[]