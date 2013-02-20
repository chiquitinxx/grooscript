package classes

/**
 * User: jorgefrancoleza
 * Date: 19/02/13
 */

class Add1 {
    static String addThis(item,text) {
        return item.text + text
    }
    static String whoAmI(item) {
        return item.class.name
    }
}

class Add2 {
    static seven(item) {
        return 7
    }
}

class Add3 {
    static four(item) {
        return 4
    }
}


class ItemToAdd {
    def text = ''
}

// Runtime mixin on String class.
// mixin() is a GDK extension to Class.
ItemToAdd.mixin Add1

assert new ItemToAdd().whoAmI() == 'classes.ItemToAdd'
assert new ItemToAdd().addThis('!!!') == '!!!'
def errors = false
try {
    assert new ItemToAdd().seven() == 7
} catch (e) {
    errors = true
}
assert errors

ItemToAdd.mixin Add2
assert new ItemToAdd().seven() == 7
assert new ItemToAdd().whoAmI() == 'classes.ItemToAdd'

def item = new ItemToAdd()
item.metaClass.mixin Add3

assert item.seven() == 7
assert item.four() == 4

errors = false
try {
    new ItemToAdd().four()
} catch (e) {
    errors = true
}
assert errors
