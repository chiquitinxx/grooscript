package advanced

/**
 * User: jorgefrancoleza
 * Date: 10/03/14
 */
class Launcher1 {
    def a
    def launch(Closure cl) {
        cl()
    }
}

class Launcher2 {
    def b
    def doIt = {
        nowPlease()
    }

    private nowPlease() {
        return 'NOW'
    }
}

Launcher1 launcher1 = new Launcher1()
Launcher2 launcher2 = new Launcher2()

assert launcher1.launch(launcher2.doIt) == 'NOW'
