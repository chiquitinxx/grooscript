
/**
 * User: jorgefrancoleza
 * Date: 20/04/13
 */

class MultiMethods {

    def hits = 0

    MultiMethods() {
        def mc = new ExpandoMetaClass(MultiMethods, false, true)
        mc.initialize()
        this.metaClass = mc
    }

    def methodMissing(String name, args) {
        hits++
        this.metaClass."$name" = { -> 1 }
        this.invokeMethod(name, args)
    }
}

def takeTime(text, closure) {
    //def timesList = [500, 2000, 10000]
    def timesList = [500]
    timesList.each { it->
        def date = new Date()
        closure(it)
        println "TakeTime (${text}) Number:${it} Time: ${new Date().time - date.time}"
    }
}

takeTime('methodMissingSpeed') { value ->
    def multi = new MultiMethods()
    Random random = new Random()
    value.times {
        multi."${random.nextInt(100000)}"()
    }
}

assert true, 'Finish Ok.'
