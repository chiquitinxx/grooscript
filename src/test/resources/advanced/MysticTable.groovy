package advanced

/**
 * JFL 17/09/12
 */
class Mystic {

    def items = []
    def closures = []

    //Add objects
    def add(object) {
        //object.metaClass.date = new Date()
        closures.each { it ->
            it object
        }
        items << object
    }

    //Insert multiple items
    def addMultiple(head,property,number) {
        number.times {
            def object = new Expando()
            object."$property" = head + it
            add object
        }
    }

    //Add a closure to apply all items gonna be added to Mystic
    def addClosureToApplyAllItems(closure) {
        closures << closure
    }
}

def mysticTable = new Mystic()

def counter = 0
//Adding insert date property to all items
mysticTable.addClosureToApplyAllItems({it-> it.metaClass.date = new Date()})
//Adding an index property to items
mysticTable.addClosureToApplyAllItems({it-> it.metaClass.index = counter++})

//Adds 10 items
mysticTable.addMultiple('Groovy ','name',10)

//Checks added correctly
assert mysticTable.items[0].name == 'Groovy 0'
assert mysticTable.items.size() == 10

//Find item inside
assert mysticTable.items.find({it -> it.name.endsWith('3')}).name == 'Groovy 3'

//Check index there
assert mysticTable.items[5].index == 5

//Insert 1 item more
def exp = ''
exp.metaClass.value = 'Eleven'
mysticTable.with {
    add(exp)
}

//Last checks
assert mysticTable.items.size() == 11
assert mysticTable.items[10].value == 'Eleven'
assert mysticTable.items[10].date > mysticTable.items[0].date
assert mysticTable.items[8].date < mysticTable.items[10].date