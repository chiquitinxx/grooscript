package advanced

/**
 * JFL 17/09/12
 */
class SuperTab {

    def items = []

    //Only can add expandos
    def add(object) {
        if (!object.date) {
            object.date = new Date()
        }
        items << object
    }

    def addMultiple(head,property,number) {
        number.times {
            def object = new Expando()
            object."$property" = head + it
            add object
        }
    }

    def getItems() {
        return items
    }

    def findItem(closure) {
        items.find(closure)
    }

    def applyClosure(closure) {
        items.each closure
    }
}

def superTable = new SuperTab()
//Adds 10 items
superTable.addMultiple('Groovy ','name',10)

assert superTable.getItems()[0].name == 'Groovy 0'
assert superTable.getItems().size() == 10

assert superTable.findItem({it -> it.name.endsWith('3')}).name == 'Groovy 3'

def index = 0
//Adding property index to all items in the supertable, cause all items are Expandos
superTable.applyClosure({it-> it.index = index++})
assert superTable.getItems()[5].index == 5

def exp = new Expando()
exp.value = 'Eleven'

//superTable.add (exp)

superTable.with {
    add(exp)
    items[10].index = 10
}

assert superTable.items.size() == 11
assert superTable.items[10].value == 'Eleven'