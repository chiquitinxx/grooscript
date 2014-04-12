package contribution

import org.grooscript.asts.GsNative

/**
 * User: jorgefrancoleza
 * Date: 29/11/13
 */

class Container {
    def item

    def initItem(value) {
        item = value
        this
    }

    @GsNative
    static dirtyItem() {/*
        return [1, 2, 3, {one: 1}];
    */}

    def isNull() {
        if (item == null) {
            true
        } else {
            false
        }
    }
}

println 'Hola ' + Container.dirtyItem()
assert ([1, 2] + 'Hey ') == [1, 2, 'Hey ']
assert ('Hey ' + [1, 2]) == 'Hey [1, 2]'

assert new Container().isNull()
assert !new Container().initItem(5).isNull()
//Only works in js
assert !new Container().initItem(Container.dirtyItem()).isNull()
