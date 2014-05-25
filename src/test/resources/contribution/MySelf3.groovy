package contribution

/**
 * Created by jorge on 24/05/14.
 */
class Executor {
    def execute(Closure cl) {
        cl()
    }
}

class CallMethods {
    def name

    def init = {
        new Executor().execute {
            setName('Me')
        }
    }
}

def callMethods = new CallMethods()
callMethods.init()
assert callMethods.name == 'Me'