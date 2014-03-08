package advanced

/**
 * User: jorgefrancoleza
 * Date: 08/03/14
 */

class Action {

    def execute(closure) {
        closure()
    }
}

class Run {
    def action = new Action()

    def run(param) {
        return 'Running ' + param
    }

    def doAction() {
        action.execute(this.&run)
    }
}

class Doing {
    def start() {
        def run = new Run()
        run.doAction()
    }
}

def doing = new Doing()
assert doing.start() == 'Running null'
