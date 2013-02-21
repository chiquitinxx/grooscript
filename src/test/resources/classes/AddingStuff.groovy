package classes

/**
 * User: jfl
 * Date: 22/01/13
 */

class Item {}

Item.metaClass.a = 5
Item.metaClass.b = {
    6
}
Item.metaClass.static.c = { 3 }

def item2 = new Item()
assert item2.a == 5
assert item2.b() == 6
assert Item.c() == 3

assert Class.forName("classes.Item").newInstance().class.name == 'classes.Item'



