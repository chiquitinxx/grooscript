package advanced

/**
 * JFL 17/09/12
 */
class SuperTab {

    def items = []

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
superTable.addMultiple('Groovy ','name',10)

assert superTable.getItems()[0].name == 'Groovy 0'
assert superTable.getItems().size() == 10

assert superTable.findItem({it -> it.name.endsWith('3')}).name == 'Groovy 3'

def index = 0
superTable.applyClosure({it-> it.index = index++})
assert superTable.getItems()[5].index == 5