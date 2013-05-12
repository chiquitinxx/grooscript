package advanced

/**
 * User: jorgefrancoleza
 * Date: 12/05/13
 */

class MethodMissing2 {

    static number = 0

    def theSize = {
        return it.size()
    }

    def callMethod(String name, args) {
        this."${name}"(args)
    }

    def methodMissing(String name, args) {
        if (name=='giveMeSize') {
            return theSize(args[0])
        } else {
            return "${name}(${args})(${number++})"
        }
    }
}

def name = 'Name'
def params = [0,2.3,'hola']

def mm = new MethodMissing2()

assert mm.callMethod(name,params) == 'Name([[0, 2.3, hola]])(0)'
assert mm.callMethod(name,null) == 'Name([null])(1)'
assert mm.callMethod('giveMeSize',params) == 3