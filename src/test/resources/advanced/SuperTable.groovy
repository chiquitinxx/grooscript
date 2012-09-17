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

    def addMultiple(head,number) {
        number.times {
            def object = new Expando()
            object.name = head + it
            object.date = new Date()
            items << object
        }
    }

    def getItems() {
        return items
    }
}

def superTable = new SuperTab()
superTable.addMultiple('Groovy ',10)

assert superTable.getItems()[0].name == 'Groovy 0'
assert superTable.getItems().size() == 10