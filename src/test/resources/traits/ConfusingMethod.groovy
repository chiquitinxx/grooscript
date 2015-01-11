package traits

/**
 * Created by jorgefrancoleza on 10/1/15.
 */
class Action {
    def call(data) {
        data
    }
}

trait WithAction {
    Action action = new Action()
}

class UseAction implements WithAction {
    def doIt() {
        action.call('hello')
    }
}

assert new UseAction().doIt() == 'hello'