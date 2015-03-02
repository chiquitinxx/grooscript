package contribution

/**
 * Created by jorge on 17/2/15.
 */

trait AngularController {

    def scope

    Closure init() {
        { controller, angularScope ->
            controller.scope = angularScope
            assert controllerProperties(controller) == ['todos']
            controllerProperties(controller).each { nameProperty ->
                angularScope."$nameProperty" = controller."$nameProperty"
            }
            assert controllerMethods(controller) == ['addTodo', 'five']
            controllerMethods(controller).each { nameMethod ->
                angularScope."$nameMethod" = controller.&"$nameMethod"
            }
        }.curry(this)
    }

    static controllerProperties(controller) {
        controller.properties.findAll { key, value ->
            !(key in ['class', 'scope'])
        }.collect { key, value -> key }
    }

    List controllerMethods(controller) {
        controller.metaClass.methods.findAll { method ->
            !method.name.startsWith('set') && !method.name.startsWith('get') && !method.name.contains('$') &&
                    !(method.name in ['equals', 'hashCode', 'notify', 'notifyAll', 'toString',
                                      'wait', 'controllerMethods', 'controllerProperties', 'init',
                                      'invokeMethod'])
        }*.name
    }
}

class TodoController implements AngularController {
    def todos = [
            [text:'learn angular', done: true],
            [text:'build an angular app', done: false]
    ]

    def addTodo() {
        scope.todos << [text: scope.todoText, done: false]
        scope.todoText = ''
    }

    def five() {
        5
    }
}

def scope = new Expando()
new TodoController().init()(scope)
assert scope.todos == new TodoController().todos
assert scope.five() == new TodoController().five()